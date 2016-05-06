package core;

/**
 * Uploader protocol functions.
 */
public class Uploader {

    public static void receivedInterested(Client client, Peer peer, Connection connection, State state) {
        if (!state.isInterested() && state.isChoked()) {
            state.setInterested(true);
        }
    }

    public static void receivedUninterested(Client client, Peer peer, Connection connection, State state) {
        if (state.isInterested() && state.isChoked()) {
            state.setInterested(false);
        }
    }

    public static void receivedRequest(Client client, Peer peer, Connection connection, State state) {
        // TODO: send piece from file (but how do we know which file? what if we're connected to this peer for more than one file?)
        if (state.isInterested() && !state.isChoked()) {
//            client.sendPiece(peer, );
        }
    }

}