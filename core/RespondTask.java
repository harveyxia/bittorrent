package core;

import message.Bitfield;
import message.Message;
import message.MessageBuilder;
import utils.Datafile;
import utils.MessageSender;

import java.util.concurrent.ConcurrentMap;

/**
 * Delegates events based on received message type.
 */
public class RespondTask implements Runnable {

    private Connection connection;
    private Message message;
    private Datafile datafile;

    private Downloader downloader;
    private Uploader uploader;
    private ConcurrentMap<Peer, Connection> connections; // only used for case PIECE_ID

    public RespondTask(Downloader downloader,
                       Uploader uploader,
                       Connection connection,
                       ConcurrentMap<Peer, Connection> connections,
                       Message message,
                       Datafile datafile) {
        this.downloader = downloader;
        this.uploader = uploader;
        this.connection = connection;
        this.connections = connections;
        this.message = message;
        this.datafile = datafile;
    }

    @Override
    public void run() {
        switch (message.getMessageID()) {
            case HANDSHAKE_ID:
                sendBitfield(connection, datafile.getBitfield());
                break;
            case INTERESTED_ID:
                // TODO: explicitly reject interested by sending choked?
                uploader.receiveInterested(connection);
                break;
            case NOT_INTERESTED_ID:
                uploader.receiveUninterested(connection);
                break;
            case HAVE_ID:
                updatePeerBitfield(connection, message.getPieceIndex());
                break;
            case REQUEST_ID:
                uploader.receiveRequest(connection, datafile, message.getPieceIndex());
                break;
            case PIECE_ID:
                downloader.receivePiece(connection, connections, datafile, message.getPiece());
                break;
            case BITFIELD_ID:
                downloader.receiveBitfield(connection, message.getBitfield());
                break;
            case CHOKE_ID:
                downloader.receiveChoke(connection);
                break;
            case UNCHOKE_ID:
                downloader.receiveUnchoke(connection, datafile);
                break;
        }
    }

    private void updatePeerBitfield(Connection connection, int pieceIndex) {
        connection.getBitfield().setPieceToCompleted(pieceIndex);
    }

    private void sendBitfield(Connection connection, Bitfield bitfield) {
        byte[] bitfieldMessage = MessageBuilder.buildBitfield(bitfield.getByteArray());
        MessageSender.sendMessage(connection.getSocket(), bitfieldMessage);
    }
}
