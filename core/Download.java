package core;

import message.MessageBuilder;
import message.Piece;
import utils.DataFile;
import utils.MessageSender;

/**
 * Handles this peer downloading from other peers.
 */
public class Download {

    public static void sendInterested(Connection connection) {
        byte[] interestedMessage = MessageBuilder.buildInterested();
        MessageSender.sendMessage(connection.getSocket(), interestedMessage);
    }

    public static void sendNotInterested(Connection connection) {
        byte[] notInterestedMessage = MessageBuilder.buildNotInterested();
        MessageSender.sendMessage(connection.getSocket(), notInterestedMessage);
    }

    public static void receiveChoke(Connection connection) {
        connection.getDownloadState().setChoked(true);
    }

    public static void receiveUnchoke(Connection connection) {
        connection.getDownloadState().setChoked(false);
    }

    public static void receivePiece(Connection connection, DataFile dataFile, Piece piece) {
        dataFile.setPieceToCompleted(piece.getPieceIndex());                // update bitfield
        dataFile.writePiece(piece.getBlock(), piece.getPieceIndex());       // write piece
    }

    // assume that pieces are downloaded in one go
    public static void sendRequest(Connection connection, int pieceIndex, int pieceLength) {
        byte[] requestMessage = MessageBuilder.buildRequest(pieceIndex, 0, pieceLength);
        MessageSender.sendMessage(connection.getSocket(), requestMessage);
    }
}
