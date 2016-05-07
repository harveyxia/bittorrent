package utils;

import core.Connection;
import core.Peer;
import message.Bitfield;
import message.MessageBuilder;
import tracker.TrackerClient;

import java.io.IOException;
import java.net.Socket;

/**
 * Aggregate all send message as static methods.
 */
public class MessageSender {

    public static void sendHandshake(Connection connection, Peer peer, Logger logger, Datafile datafile, TrackerClient trackerClient) {
        byte[] handshakeMessage = MessageBuilder.buildHandshake(datafile.getFilename(), trackerClient.getClient());
        sendMessage(connection.getSocket(), handshakeMessage);
        logger.log("send HANDSHAKE to " + peer);
    }

    public static void sendUnchoke(Connection connection, Peer peer, Logger logger) {
        byte[] unchokeMessage = MessageBuilder.buildUnchoke();
        sendMessage(connection.getSocket(), unchokeMessage);
        connection.getUploadState().setChoked(false);
        logger.log("send UNCHOKE to " + peer);
    }

    public static void sendChoke(Connection connection, Peer peer, Logger logger) {
        byte[] chokeMessage = MessageBuilder.buildChoke();
        sendMessage(connection.getSocket(), chokeMessage);
        connection.getUploadState().setChoked(true);
        logger.log("send CHOKE to " + peer);
    }

    public static void sendPiece(Connection connection, Peer peer, Logger logger, int pieceIndex, Datafile datafile) {
        byte[] pieceMessage = MessageBuilder.buildPiece(pieceIndex, 0, datafile.readPiece(pieceIndex));
        sendMessage(connection.getSocket(), pieceMessage);
        logger.log(String.format("send PIECE for pieceIndex:%d to " + peer, pieceIndex));
    }

    public static void sendBitfield(Connection connection, Peer peer, Logger logger, Bitfield bitfield) {
        byte[] bitfieldMessage = MessageBuilder.buildBitfield(bitfield.getByteArray());
        MessageSender.sendMessage(connection.getSocket(), bitfieldMessage);
        logger.log("send BITFIELD " + bitfield + " to " + peer);
    }

    public static void sendInterested(Connection connection, Peer peer, Logger logger) {
        byte[] interestedMessage = MessageBuilder.buildInterested();
        MessageSender.sendMessage(connection.getSocket(), interestedMessage);
        connection.getDownloadState().setInterested(true);
        logger.log("send INTERESTED to " + peer);
    }

    public static void sendNotInterested(Connection connection, Peer peer, Logger logger) {
        byte[] notInterestedMessage = MessageBuilder.buildNotInterested();
        MessageSender.sendMessage(connection.getSocket(), notInterestedMessage);
        connection.getDownloadState().setInterested(false);
        logger.log("send NOT_INTERESTED to " + peer);
    }

    public static void sendHave(Connection connection, Peer peer, Logger logger, int pieceIndex) {
        byte[] haveMessage = MessageBuilder.buildHave(pieceIndex);
        MessageSender.sendMessage(connection.getSocket(), haveMessage);
        logger.log(String.format("send HAVE for pieceIndex:%d to " + peer, pieceIndex));
    }

    // assume that pieces are downloaded in one go
    public static void sendRequest(Connection connection, Peer peer, Logger logger, int pieceIndex, int pieceLength) {
        if (!connection.canDownloadFrom()) {
            logger.log("ERROR: cannot download from " + peer);
            return;
        }
        byte[] requestMessage = MessageBuilder.buildRequest(pieceIndex, 0, pieceLength);
        MessageSender.sendMessage(connection.getSocket(), requestMessage);
        logger.log(String.format("send REQUEST for pieceIndex:%d, pieceLength:%d to " + peer, pieceIndex, pieceLength));
    }

    private static void sendMessage(Socket peerSocket, byte[] message) {
        try {
            peerSocket.getOutputStream().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
