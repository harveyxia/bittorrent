package core;

import message.Bitfield;
import message.MessageBuilder;
import message.Piece;
import utils.Datafile;
import utils.Logger;
import utils.MessageSender;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles this peer downloading from other peers.
 */
public class Download {

    private Logger logger;

    public Download(Logger logger) {
        this.logger = logger;
    }

    public void sendInterested(Connection connection) {
        byte[] interestedMessage = MessageBuilder.buildInterested();
        MessageSender.sendMessage(connection.getSocket(), interestedMessage);
        logger.log(" send INTERESTED to " + connection.getSocket());
    }

    public void sendNotInterested(Connection connection) {
        byte[] notInterestedMessage = MessageBuilder.buildNotInterested();
        MessageSender.sendMessage(connection.getSocket(), notInterestedMessage);
        logger.log(" send NOT_INTERESTED to " + connection.getSocket());
    }

    public void sendHave(Connection connection, int pieceIndex) {
        byte[] haveMessage = MessageBuilder.buildHave(pieceIndex);
        MessageSender.sendMessage(connection.getSocket(), haveMessage);
        logger.log(String.format(" send HAVE for pieceIndex:%d to " + connection.getSocket(), pieceIndex));
    }

    public void receiveChoke(Connection connection) {
        connection.getDownloadState().setChoked(true);
        logger.log(" receive CHOKE from " + connection.getSocket());
    }

    public void receiveUnchoke(Connection connection, Datafile datafile) {
        connection.getDownloadState().setChoked(false);
        logger.log(" receive UNCHOKE from " + connection.getSocket());
        requestFirstAvailPiece(connection, datafile);   // request first available piece
    }

    public void receivePiece(Connection connection,
                             ConcurrentMap<Peer, Connection> connections,
                             Datafile datafile,
                             Piece piece) {
        datafile.getBitfield().setPieceToCompleted(piece.getPieceIndex());                // update bitfield
        datafile.writePiece(piece.getBlock(), piece.getPieceIndex());       // write piece
        // Broadcast HAVE message for new pieceIndex to all other peers
        for (Map.Entry<Peer, Connection> peerConnection : connections.entrySet()) {
            if (!peerConnection.getValue().equals(connection)) {
                sendHave(peerConnection.getValue(), piece.getPieceIndex());
                logger.log(" send HAVE to " + peerConnection.getKey());
            }
        }
    }

    public void receiveBitfield(Connection connection, Bitfield bitfield) {
        logger.log(" receive BITFIELD " + bitfield + " from " + connection.getSocket().getInetAddress());
        connection.setBitfield(bitfield);               // set peer's bitfield
        sendInterested(connection);
    }

    // assume that pieces are downloaded in one go
    public void sendRequest(Connection connection, int pieceIndex, int pieceLength) {
        byte[] requestMessage = MessageBuilder.buildRequest(pieceIndex, 0, pieceLength);
        MessageSender.sendMessage(connection.getSocket(), requestMessage);
        logger.log(String.format(" send REQUEST for pieceIndex:%d, pieceLength:%d to " +
                connection.getSocket().getInetAddress(), pieceIndex, pieceLength));
    }

    /**
     * Requests from peer the first missing piece that peer has.
     */
    private void requestFirstAvailPiece(Connection connection, Datafile datafile) {
        for (int i = 0; i < datafile.getNumPieces(); i++) {
            if (datafile.getBitfield().missingPiece(i) && connection.getBitfield().hasPiece(i)) {
                sendRequest(connection, i, datafile.getPieceLength()); // request entire piece
                break;
            }
        }
    }
}
