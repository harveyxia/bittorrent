package core;

import tracker.TrackerRequest;
import tracker.TrackerResponse;
import utils.DataFile;
import utils.MessageBuilder;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;

import static utils.MessageParser.getMessageId;

/**
 * Bittorrent client.
 */
public class Client {

    private static final int BACKLOG = 10;
    private static final int CLIENT_PORT = 200;
    private static final int SERVER_PORT = 300;
    private static Inet4Address trackerAddr;
    private static int trackerPort;
    private HashMap<Inet4Address, ConnectionState> peers;

    private void onReceiveMessage(byte[] message) {
        MessageBuilder.MessageId messageId = getMessageId(message);
        switch (messageId) {
            case CHOKE_ID:
                break;
            case INTERESTED_ID:
                break;
            case NOT_INTERESTED_ID:
                break;
            case HAVE_ID:
                break;
            case REQUEST_ID:
                break;
            case PIECE_ID:
                break;
            case BITFIELD_ID:
                break;
        }
    }

    public Client() {
        this.peers = new HashMap<>();
    }

    public static void main(String[] argv) {
    }

    public void listen() {
        // TODO: start server socket thread listening for peer connections
        try (ServerSocket socket = new ServerSocket(SERVER_PORT, BACKLOG)) {

            // Run server thread
            Thread serverThread = new Thread(new ClientListener(socket));
            serverThread.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TrackerResponse getTrackerResponse(String filename) throws IOException {

        Socket socket = new Socket(trackerAddr, trackerPort);

        // Send tracker request
        TrackerRequest request = new TrackerRequest(TrackerRequest.Event.STARTED, (InetSocketAddress) socket.getLocalSocketAddress(), filename);
        request.send(socket.getOutputStream());

        // Receive tracker response
        return TrackerResponse.fromStream(socket.getInputStream());
    }

    public void connect(Inet4Address peer) {
        // TODO: setup TCP connection with peer
        Inet4Address localAddr;

        try {
            localAddr = (Inet4Address) InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        try (Socket socket = new Socket(peer, SERVER_PORT)) {
            peers.put(peer, ConnectionState.getInitialState());
            // TODO: send desired filename
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void sendHave(Inet4Address peer) {
    }

    public void sendChoke(Inet4Address peer) {
    }

    public void sendUnchoke(Inet4Address peer) {
    }

    public void sendInterested(Inet4Address peer) {
    }

    public void sendUninterested(Inet4Address peer) {
    }

    // TODO: are we implementing keep-alive messages?
    public void sendKeepAlive(Inet4Address peer) {
    }

    public void sendRequest(Inet4Address peer, int index, int begin, int length) {
    }

    public void sendPiece(Inet4Address peer, DataFile file, int index, int begin, int length) {
    }

    // TODO: do we need this if not implementing end-game behavior?
    public void sendCancel(Inet4Address peer) {
    }
}
