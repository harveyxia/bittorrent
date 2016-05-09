package core;

import message.Message;
import utils.Datafile;
import utils.MessageSender;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Delegates events based on received message type.
 */
public class RespondTask implements Runnable {

    private ConcurrentHashMap<Peer, Float> unchokedPeers;
    private Connection connection;
    private Message message;
    private Datafile datafile;

    private Downloader downloader;
    private Uploader uploader;
    private Peer peer;
    private ConcurrentMap<Peer, Connection> connections; // only used for case PIECE_ID

    public RespondTask(Downloader downloader,
                       Uploader uploader,
                       Connection connection,
                       Peer peer,
                       ConcurrentMap<Peer, Connection> connections,
                       Message message,
                       Datafile datafile) {
        this.downloader = downloader;
        this.uploader = uploader;
        this.peer = peer;
        this.connection = connection;
        this.connections = connections;
        this.message = message;
        this.datafile = datafile;
    }

    @Override
    public void run() {
        switch (message.getMessageID()) {
            case HANDSHAKE_ID:
                MessageSender.sendBitfield(connection, peer, downloader.getLogger(), datafile.getBitfield());
                connection.getSocket().getInetAddress();
                        connection.getSocket().getPort();
                break;
            case INTERESTED_ID:
                uploader.receiveInterested(connection, peer);
                break;
            case NOT_INTERESTED_ID:
                uploader.receiveUninterested(connection, peer);
                break;
            case HAVE_ID:
                updatePeerBitfield(connection, message.getPieceIndex());
                break;
            case REQUEST_ID:
                uploader.receiveRequest(connection, peer, datafile, message.getRequest().getPieceIndex());
                break;
            case PIECE_ID:
                downloader.receivePiece(connection, peer, connections, datafile, message.getPiece());
                break;
            case BITFIELD_ID:
                downloader.receiveBitfield(connection, peer, message.getBitfield(), datafile);
                break;
            case CHOKE_ID:
                downloader.receiveChoke(connection, peer);
                break;
            case UNCHOKE_ID:
                downloader.receiveUnchoke(connection, peer, datafile);
                break;
        }
    }

    private void updatePeerBitfield(Connection connection, int pieceIndex) {
        connection.getBitfield().setPieceToCompleted(pieceIndex);
    }


}
