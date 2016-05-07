package core;

import message.Bitfield;
import message.Piece;
import utils.Datafile;
import utils.Logger;
import utils.MessageSender;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles this peer downloading from other peers.
 */
public class Downloader {

    private Logger logger;

    public Downloader(Logger logger) {
        this.logger = logger;
    }

    public void receiveChoke(Connection connection) {
        connection.getDownloadState().setChoked(true);
        logger.log("receive CHOKE from " + connection.getSocket());
    }

    public void receiveUnchoke(Connection connection, Datafile datafile) {
        connection.getDownloadState().setChoked(false);
        logger.log("receive UNCHOKE from " + connection.getSocket());
        requestFirstAvailPiece(connection, datafile);   // request first available piece
    }

    public void receivePiece(Connection connection,
                             ConcurrentMap<Peer, Connection> connections,
                             Datafile datafile,
                             Piece piece) {
        datafile.getBitfield().setPieceToCompleted(piece.getPieceIndex());                  // 1. update bitfield
        datafile.writePiece(piece.getBlock(), piece.getPieceIndex());                       // 2. write piece
        connection.incrementBytesDownloaded(piece.getBlock().length);                       // 3. update bytes downloaded
        for (Map.Entry<Peer, Connection> peerConnection : connections.entrySet()) {         // 4. broadcast Have new piece to all peers
            if (!peerConnection.getValue().equals(connection)) {
                MessageSender.sendHave(peerConnection.getValue(), logger, piece.getPieceIndex());
                logger.log("send HAVE to " + peerConnection.getKey());
            }
        }
        requestFirstAvailPiece(connection, datafile);                                        // 5. request next piece
        // TODO: what to do when completed
    }

    public void receiveBitfield(Connection connection, Bitfield bitfield, Datafile datafile) {
        logger.log("receive BITFIELD " + bitfield + " from " + getPeerIpPort(connection));
        connection.setBitfield(bitfield);               // set peer's bitfield
        if (!datafile.isCompleted()) {
            MessageSender.sendInterested(connection, logger);
        }
    }

    /**
     * Requests from peer the first missing piece that peer has.
     */
    private void requestFirstAvailPiece(Connection connection, Datafile datafile) {
        if (datafile.isCompleted()) {
            logger.log("datafile is complete! WOOOOOOOOOOOO!");
            return;
        }
        for (int i = 0; i < datafile.getNumPieces(); i++) {
            if (datafile.getBitfield().missingPiece(i) && connection.getBitfield().hasPiece(i)) {
                MessageSender.sendRequest(connection, logger, i, datafile.getPieceLength()); // request entire piece
                break;
            }
        }
    }

    private String getPeerIpPort(Connection connection) {
        return connection.getSocket().getInetAddress() + ":" + connection.getSocket().getPort();
    }

    public Logger getLogger() {
        return logger;
    }
}
