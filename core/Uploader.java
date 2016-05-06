package core;

import message.MessageBuilder;
import utils.DataFile;
import utils.Logger;
import utils.MessageSender;

/**
 * Uploader protocol functions.
 */
public class Uploader {

    private Logger logger;

    public Uploader(Logger logger) {
        this.logger = logger;
    }

    public void receiveInterested(Connection connection) {
        logger.log(" receive INTERESTED from " + connection.getSocket());
        State state = connection.getUploadState();
        if (!state.isInterested() && state.isChoked()) {
            state.setInterested(true);
        }
    }

    public void receiveUninterested(Connection connection) {
        logger.log(" receive UNINTERESTED from " + connection.getSocket());
        State state = connection.getUploadState();
        if (state.isInterested() && state.isChoked()) {
            state.setInterested(false);
        }
    }

    public void receiveRequest(Connection connection, DataFile dataFile, int pieceIndex) {
        if (!connection.canUploadTo()) {
            logger.log(" ERROR: cannot upload to " + connection.getSocket().getInetAddress());
            return;
        }
        byte[] pieceMessage = MessageBuilder.buildPiece(pieceIndex, 0, dataFile.readPiece(pieceIndex));
        MessageSender.sendMessage(connection.getSocket(), pieceMessage);
        logger.log(String.format(" receive REQUEST for pieceIndex:%d from " +
                connection.getSocket().getInetAddress(), pieceIndex));
    }

}