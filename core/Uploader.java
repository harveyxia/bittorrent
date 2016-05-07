package core;

import message.MessageBuilder;
import utils.Datafile;
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
        logger.log(" receive INTERESTED from " + getPeerIpPort(connection));
        State state = connection.getUploadState();
        if (!state.isInterested() && state.isChoked()) {
            state.setInterested(true);
        }
    }

    public void receiveUninterested(Connection connection) {
        logger.log(" receive UNINTERESTED from " + getPeerIpPort(connection));
        State state = connection.getUploadState();
        if (state.isInterested() && state.isChoked()) {
            state.setInterested(false);
        }
    }

    public void receiveRequest(Connection connection, Datafile datafile, int pieceIndex) {
        if (!connection.canUploadTo()) {
            logger.log(" ERROR: cannot upload to " + getPeerIpPort(connection));
            return;
        }
        byte[] pieceMessage = MessageBuilder.buildPiece(pieceIndex, 0, datafile.readPiece(pieceIndex));
        MessageSender.sendMessage(connection.getSocket(), pieceMessage);
        logger.log(String.format(" receive REQUEST for pieceIndex:%d from " +
                getPeerIpPort(connection), pieceIndex));
    }

    private String getPeerIpPort(Connection connection) {
        return connection.getSocket().getInetAddress() + ":" + connection.getSocket().getPort();
    }
}
