# Bittorrent

Charles Jin, Jason Kim, Marvin Qian, Harvey Xia

## Summary

This project is an implementation of the Bittorrent protocol. This includes a peer application that contacts a tracker server and exchanges data with other peers.

### Design Goals

In this implementation, we sought to implement the peer-to-tracker and peer-to-peer Bittorrent protocols. Interestingly, the official Bittorrent specification offers only a high-level description of these protocols, so we've designed and implemented protocols that meet the specification's requirements. We describe these protocols in more detail in further sections.

### Problems Encountered

- Peers would sometimes attempt to simultaneously connect with one another. When this occurred, it was possible for two connections (one in each direction) to exist between to peers simultaneously, which is incorrect. To fix this issue, we added a policy so that only newly joined peers are responsible for initiating connections. This eliminates the race condition. However, this requires that newly joined peers connect with all other peers. While this would be infeasible for very large peer lists, at the scale of this implementation this design decision causes no issues.
- The Bittorrent protocol specifies using a unique identifier for each peer. At first we thought this was only necessitated by local IP and NAT translation. Later we realized that we need a unique indentifier because each of the client's outgoing connections is bound to a random port that happens to be available. This means we can't rely on the values of `socket.getInetAddress()` and `socket.getPort()` to identify a peer. Instead throughout the application we identify peers by their IP and welcome socket port.

## How To Use

To make: `make`

To test: `./runtests.sh`

## Project Structure

The project is implemented in two primary packages: core and tracker. The core package contains code for the client application and peer-to-peer communication. The tracker package contains code for the tracker and peer-to-tracker communication.

metafile/Metafile.java - object representing torrent metafile, i.e. the .torrent file. Also parses a file and returns a Metafile object

tracker/Tracker.java - the server that responds to requests from peers

core/Client.java - the bittorrent peer

core/Unchoker.java - Runnable that periodically runs the unchoke algorithm and updates the list of unchoked peers

message/* - Contains various classes that build and parse bittorrent messages over TCP

utils/Datafile.java - object encapsulating data file, i.e. the file that is being downloaded and uploaded

utils/Logger.java - Logging object

## Sources

This implementation follows the protocol as described [here](https://wiki.theory.org/BitTorrentSpecification).

## Run

In order to run a basic tracker + seeder + leecher setup, run each of the following commands in a new terminal and in the order shown below:

#### Tracker

`java tracker.Tracker 6789`

#### Seeder

`java -classpath .:lib/json-20160212.jar:lib/junit-4.12.jar core.Client Client1 6000 tests/test.torrent tests/dataFolder1 registerFile`

#### Leecher

`java -classpath .:lib/json-20160212.jar:lib/junit-4.12.jar core.Client Client2 7000 tests/test.torrent tests/dataFolder2`

## Design



### Client

The client serves as the interface for sharing and downloading a file. If the user has a file to share, he can advertise the file to the tracker and then share a torrent file so downloaders can find the file. If the user wants to download a file, he just needs to initiate the client with the associated torrent file. Downloading or uploading multiple files simply involves running multiple client instances--one for each file.

The implementation of the client consists of two main threads that can create separate tasks to be run on a thread pool. One of these threads simply presents a welcome socket and accepts new connections from peers. The other thread parses messages from the connection sockets of all connected peers. Once the messages are parsed, they are bundled into a task instance and passed to the thread pool for processing. In addition to these tasks, the client also has to query the tracker periodically for the updated peer list. This is done through scheduled tasks and allows the client to be robust to peer churn since it will quickly discover peers that have joined or left. Finally, an unchoker task also runs periodically in order to update which peers the client has decided to unchoke (i.e., upload to them).

For each peer (IP-port pair), the client keeps a set of connection information. This information includes the associated socket, the peer's bitfield, his upload rate to the client and download rate from the client, and choke/unchoke and interested/uninterested state for the protocol.

We implemented the peer-to-peer protocol using two finite state machines, one for the download actions of a client and one for the upload actions of a client, as shown below. For each file, the client maintains a list of four peers. If the client hasn't completed downloading the file, then this list contains the four peers that have provided the fastest *download* rates. If the client has completed downloading the file (i.e., is now seeding), then the list contains the four peers that have provided the fastest *upload* rates. This tit-for-tat strategy provides incentive to upload quickly to your peers in order to receive more data in return.

#### Downloader FSM

Note: C = peerChoking and I = amInterested.

![downloader FSM](downloader-fsm-01.svg)

#### Uploader FSM

Note: C = amChoking and I = peerInterested.

![uploader FSM](uploader-fsm-01.svg)

### Tracker

## Future Directions

Our implementation simplifies the protocol in a few ways:

- Instead of downloading a piece from a peer one block at a time, we download a piece all at once. Extending our implementation would involve sending and receiving data at block granularity.
- We currently do not implement SHA1 verification of pieces. Implementing this feature would involve the receiving client, upon receiving a complete piece, to verify the integrity of that piece by computing its SHA1 hash and comparing it against the SHA1 hash for that piece as defined in the torrent metafile.
- We request pieces in simple sequential order instead of seeking the rarest piece first.
- We do not implement optimistic unchoking. This does not affect files with at most 4 peers, which suffices for the tests that we've written.
- Upon joining, a peer connects will all other peers. We could instead contact a fixed number of peers and allow any peer to initiate a connection with any other peer.

Implementing any of these features would improve the speed, scalability, or robustness of the application.