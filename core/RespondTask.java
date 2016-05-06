package core;

import message.Message;
import utils.DataFile;

/**
 * Delegates events based on received message type.
 */
public class RespondTask implements Runnable {

    private Connection connection;
    private Message message;
    private DataFile datafile;

    public RespondTask(Connection connection, Message message, DataFile datafile) {
        this.connection = connection;
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
                Download.receivePiece(connection, datafile, message.getPiece());
                // TODO: broadcast HAVE to all peers
                break;
            case BITFIELD_ID:
                break;
            case HANDSHAKE_ID:
                break;
            case CHOKE_ID:
                Download.receiveChoke(connection);
                break;
            case UNCHOKE_ID:
                Download.receiveUnchoke(connection);
                break;
        }
    }
}
