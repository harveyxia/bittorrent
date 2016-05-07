## How To Use

To make:
$ make

To test:
$ ./runtests.sh

## Project Structure

TorrentParser.java - parses a file and returns a Torrent object
Torrent.java - object containing .torrent file fields

Tracker.java - HTTP server that responds to GET requests

Client.java - bittorrent peer

MessageUtils - creates and parses bittorrent messages

## Sources

This implementation follows the protocol as described [here](https://wiki.theory.org/BitTorrentSpecification).

## Run

`java tracker.Tracker 6789`

`java -classpath .:lib/json-20160212.jar:lib/junit-4.12.jar core.Client Client1 6000 test.torrent tests/dataFolder1 registerFile`

`java -classpath .:lib/json-20160212.jar:lib/junit-4.12.jar core.Client Client2 7000 test.torrent tests/dataFolder2`
