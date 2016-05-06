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
