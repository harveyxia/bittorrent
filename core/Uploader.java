package core;

import utils.Datafile;
import utils.Logger;
import utils.MessageSender;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Uploader protocol functions.
 */
public class Uploader {

    private Logger logger;
    private ConcurrentHashMap<Peer, Float> unchokedPeers;

    public Uploader(Logger logger, ConcurrentHashMap<Peer, Float> unchokedPeers) {
        this.logger = logger;
        this.unchokedPeers = unchokedPeers;
    }

    public void receiveInterested(Connection connection, Peer peer) {
        logger.log(" receive INTERESTED from " + connection.getSocket());
        State state = connection.getUploadState();
        //        if (!state.isInterested() && state.isChoked()) {
        state.setInterested(true);
        for (Peer peer1 : unchokedPeers.keySet()) {
            System.out.println("Uploaded unchoked set contains " + peer1);
        }
        if (unchokedPeers.containsKey(peer)) {
            MessageSender.sendUnchoke(connection, logger);
        }
        //        }
    }

    public void receiveUninterested(Connection connection) {
        logger.log("receive UNINTERESTED from " + connection.getSocket());
        State state = connection.getUploadState();
        //        if (state.isInterested() && state.isChoked()) {
        state.setInterested(false);
        //        }
    }

    public void receiveRequest(Connection connection, Datafile datafile, int pieceIndex) {
        logger.log(String.format("receive REQUEST for pieceIndex:%d from " + connection.getSocket(), pieceIndex));
        if (!connection.canUploadTo()) {
            logger.log("ERROR: cannot upload to " + connection.getSocket());
            return;
        }
        MessageSender.sendPiece(connection, logger, pieceIndex, datafile);
    }

    public ConcurrentHashMap<Peer, Float> getUnchokedPeers() {
        return unchokedPeers;
    }

}
