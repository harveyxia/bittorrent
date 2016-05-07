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
        logger.log(" receive INTERESTED from " + peer);
        State state = connection.getUploadState();
        //        if (!state.isInterested() && state.isChoked()) {
        state.setInterested(true);
        for (Peer peer1 : unchokedPeers.keySet()) {
            System.out.println("Uploaded unchoked set contains " + peer1);
        }
        if (unchokedPeers.containsKey(peer)) {
            MessageSender.sendUnchoke(connection, peer, logger);
        }
        //        }
    }

    public void receiveUninterested(Connection connection, Peer peer) {
        logger.log("receive UNINTERESTED from " + peer);
        State state = connection.getUploadState();
        //        if (state.isInterested() && state.isChoked()) {
        state.setInterested(false);
        //        }
    }

    public void receiveRequest(Connection connection, Peer peer, Datafile datafile, int pieceIndex) {
        logger.log(String.format("receive REQUEST for pieceIndex:%d from " + peer, pieceIndex));
        if (!connection.canUploadTo()) {
            logger.log("ERROR: cannot upload to " + peer);
            return;
        }
        MessageSender.sendPiece(connection, peer, logger, pieceIndex, datafile);
    }

    public ConcurrentHashMap<Peer, Float> getUnchokedPeers() {
        return unchokedPeers;
    }

}
