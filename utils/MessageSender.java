package utils;

import core.Connection;
import message.Bitfield;
import message.MessageBuilder;
import tracker.TrackerClient;

import java.io.IOException;
import java.net.Socket;

/**
 * Aggregate all send message as static methods.
 */
public class MessageSender {

    public static void sendHandshake(Connection connection, Logger logger, Datafile datafile, TrackerClient trackerClient) {
        byte[] handshakeMessage = MessageBuilder.buildHandshake(datafile.getFilename(), trackerClient.getClient());
        sendMessage(connection.getSocket(), handshakeMessage);
        logger.log("send HANDSHAKE to " + connection.getSocket());
    }

    public static void sendUnchoke(Connection connection, Logger logger) {
        byte[] unchokeMessage = MessageBuilder.buildUnchoke();
        sendMessage(connection.getSocket(), unchokeMessage);
        connection.getUploadState().setChoked(false);
        logger.log("send UNCHOKE to " + connection.getSocket());
    }

    public static void sendChoke(Connection connection, Logger logger) {
        byte[] chokeMessage = MessageBuilder.buildChoke();
        sendMessage(connection.getSocket(), chokeMessage);
        connection.getUploadState().setChoked(true);
        logger.log("send CHOKE to " + connection.getSocket());
    }

    public static void sendPiece(Connection connection, Logger logger, int pieceIndex, Datafile datafile) {
        byte[] pieceMessage = MessageBuilder.buildPiece(pieceIndex, 0, datafile.readPiece(pieceIndex));
        sendMessage(connection.getSocket(), pieceMessage);
        logger.log(String.format("send PIECE for pieceIndex:%d to " + connection.getSocket(), pieceIndex));
    }

    public static void sendBitfield(Connection connection, Logger logger, Bitfield bitfield) {
        byte[] bitfieldMessage = MessageBuilder.buildBitfield(bitfield.getByteArray());
        MessageSender.sendMessage(connection.getSocket(), bitfieldMessage);
        logger.log("send BITFIELD " + bitfield + " to " + connection.getSocket());
    }

    public static void sendInterested(Connection connection, Logger logger) {
        byte[] interestedMessage = MessageBuilder.buildInterested();
        MessageSender.sendMessage(connection.getSocket(), interestedMessage);
        connection.getDownloadState().setInterested(true);
        logger.log("send INTERESTED to " + connection.getSocket());
    }

    public static void sendNotInterested(Connection connection, Logger logger) {
        byte[] notInterestedMessage = MessageBuilder.buildNotInterested();
        MessageSender.sendMessage(connection.getSocket(), notInterestedMessage);
        connection.getDownloadState().setInterested(false);
        logger.log("send NOT_INTERESTED to " + connection.getSocket());
    }

    public static void sendHave(Connection connection, Logger logger, int pieceIndex) {
        byte[] haveMessage = MessageBuilder.buildHave(pieceIndex);
        MessageSender.sendMessage(connection.getSocket(), haveMessage);
        logger.log(String.format("send HAVE for pieceIndex:%d to " + connection.getSocket(), pieceIndex));
    }

    // assume that pieces are downloaded in one go
    public static void sendRequest(Connection connection, Logger logger, int pieceIndex, int pieceLength) {
        if (!connection.canDownloadFrom()) {
            logger.log("ERROR: cannot download from " + connection.getSocket());
            return;
        }
        byte[] requestMessage = MessageBuilder.buildRequest(pieceIndex, 0, pieceLength);
        MessageSender.sendMessage(connection.getSocket(), requestMessage);
        logger.log(String.format(" send REQUEST for pieceIndex:%d, pieceLength:%d to " + connection.getSocket(), pieceIndex, pieceLength));
    }

    private static void sendMessage(Socket peerSocket, byte[] message) {
        try {
            peerSocket.getOutputStream().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
