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
        State state = connection.getUploadState();
        if (!state.isInterested() && state.isChoked()) {
            state.setInterested(true);
        }
    }

    public void receiveUninterested(Connection connection) {
        State state = connection.getUploadState();
        if (state.isInterested() && state.isChoked()) {
            state.setInterested(false);
        }
    }

    public void receiveRequest(Connection connection, DataFile dataFile, int pieceIndex) {
        if (connection.canUploadTo()) {
            byte[] pieceMessage = MessageBuilder.buildPiece(pieceIndex, 0, dataFile.readPiece(pieceIndex));
            MessageSender.sendMessage(connection.getSocket(), pieceMessage);
        }
    }

}