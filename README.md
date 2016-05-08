# Bittorrent

This project is an implementation of the Bittorrent protocol. This includes a peer application that contacts a tracker server and exchanges data with other peers.

## How To Use

To make:
$ make

To test:
$ ./runtests.sh

## Project Structure

The project is implement in two primary packages: core and tracker. The core package contains code for the client application and peer-to-peer communication. The tracker package contains code for the tracker and client-to-tracker communication.

TorrentParser.java - parses a file and returns a Torrent object

Torrent.java - object containing .torrent file fields

Tracker.java - HTTP server that responds to GET requests

Client.java - bittorrent peer

MessageUtils - creates and parses bittorrent messages

## Sources

This implementation follows the protocol as described [here](https://wiki.theory.org/BitTorrentSpecification).

## Run

In order to run a basic tracker + seeder + leecher setup, run each of the following in a separate terminal in the order below:

#### Tracker

`java tracker.Tracker 6789`

#### Seeder

`java -classpath .:lib/json-20160212.jar:lib/junit-4.12.jar core.Client Client1 6000 tests/test.torrent tests/dataFolder1 registerFile`

#### Leecher

`java -classpath .:lib/json-20160212.jar:lib/junit-4.12.jar core.Client Client2 7000 tests/test.torrent tests/dataFolder2`

## Design



### Client

The client is composed of two main parts. One part handles outgoing connections to the tracker and peers. The other presents a welcome socket that handles incoming connections from other peers. These two parts of the client share a mapping from IP-port pairs to connection information. We identify a peer instance by its IP address and welcome socket port number.

### Tracker

