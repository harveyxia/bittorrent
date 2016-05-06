package core;

import message.Message;
import utils.Datafile;
import utils.Logger;

import java.util.concurrent.ConcurrentMap;

/**
 * Delegates events based on received message type.
 */
public class RespondTask implements Runnable {

    private Connection connection;
    private Message message;
    private Datafile datafile;

    private Download download;
    private ConcurrentMap<Peer, Connection> connections; // only used for case PIECE_ID

    public RespondTask(Download download,
                       Connection connection,
                       ConcurrentMap<Peer, Connection> connections,
                       Message message,
                       Datafile datafile) {
        this.download = download;
        this.connection = connection;
        this.connections = connections;
        this.message = message;
        this.datafile = datafile;
    }

    @Override
    public void run() {
        switch (message.getMessageID()) {
            case INTERESTED_ID:
                break;
            case NOT_INTERESTED_ID:
                break;
            case HAVE_ID:
                break;
            case REQUEST_ID:
                break;
            case PIECE_ID:
                download.receivePiece(connection, connections, datafile, message.getPiece());
                break;
            case BITFIELD_ID:
                break;
            case HANDSHAKE_ID:
                break;
            case CHOKE_ID:
                download.receiveChoke(connection);
                break;
            case UNCHOKE_ID:
                download.receiveUnchoke(connection);
                break;
        }
    }
}
