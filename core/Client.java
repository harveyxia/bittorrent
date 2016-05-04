package core;

import message.MessageBuilder;
import tracker.TrackerRequest;
import tracker.TrackerResponse;
import utils.DataFile;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Bittorrent client.
 */
public class Client {

    public static final int NUM_THREADS = 2;
    private static final int BACKLOG = 10;
    private static final String CMD_USAGE = "java Client clientName clientPort serverPort";
    private static final int CLIENT_PORT = 200;
    private static final int SERVER_PORT = 300;
    private static Inet4Address trackerAddr;
    private static int trackerPort;

    private HashMap<Peer, ConnectionState> connectionStates;    // maintain bittorrent state of each p2p connection
    private HashMap<Peer, Socket> connections;                  // maintain TCP state of each p2p connection
    private HashMap<String, DataFile> files;                    // maintain map of filenames to files
    private HashSet<Peer> peers;                                // list of all peers
    private int clientPort;
    private int listenPort;
    private String clientName;

    public Client(String clientName, int clientPort, int listenPort) {
        this.connectionStates = new HashMap<>();
        this.connections = new HashMap<>();
        this.clientName = clientName;
        this.clientPort = clientPort;
        this.listenPort = listenPort;
        logOutput("listening on port " + listenPort);
        logOutput("downloading on port " + clientPort);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(CMD_USAGE);
            return;
        }
        int clientPort = Integer.parseInt(args[1]);
        int listenPort = Integer.parseInt(args[2]);
        Client client = new Client(args[0], clientPort, listenPort);
        Thread listenThread = client.getListenThread();
        Thread downloadThread = client.getDownloadThread("fileId", "trackerUrl");
        ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS);
        service.submit(listenThread);
        service.submit(downloadThread);
    }

    /**
     * Listen for incoming client connections.
     */
    public Thread getListenThread() {
        Thread serverThread = null;
        try {
            ServerSocket socket = new ServerSocket(listenPort, BACKLOG);
            // Run server thread
            serverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        // Accept peer connections
                        try (Socket inConn = socket.accept()) {
                            logOutput("accepted new connection from " + inConn.getInetAddress() + " at port " + inConn.getPort());
                            Peer peer = new Peer(inConn.getInetAddress(), inConn.getPort());
                            connections.put(peer, inConn);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Process any new messages
                        for (Peer peer : peers) {
                            ConnectionState state = connectionStates.get(peer);
                            if (state == null) continue;

                            if (state.isEstablished()) {        // Piece exchange
                                // Piece exchange
                            } else {                            // Handshake
                                // 1. Receive handshake message
                                // 2. Send handshake message
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverThread;
    }

    /**
     * Download a file by contacting tracker and connecting to peers.
     */
    public Thread getDownloadThread(String fileId, String trackerUrl) {
        Thread downloadThread;
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 1. contact tracker
                // 2. handshake peers
                // 3. event loop for each out going connection
                while (true) {
                    for (Peer peer : connectionStates.keySet()) {
                        // Determine action based on state
                        // Send message
                    }
                }
            }
        });
        return downloadThread;
    }

    public TrackerResponse getTrackerResponse(String filename) throws IOException {

        Socket socket = new Socket(trackerAddr, trackerPort);

        // Send tracker request
        TrackerRequest request = new TrackerRequest(TrackerRequest.Event.STARTED, (InetSocketAddress) socket.getLocalSocketAddress(), filename);
        request.send(socket.getOutputStream());

        // Receive tracker response
        return TrackerResponse.fromStream(socket.getInputStream());
    }

    public void handshake(Peer peer, String filename) {
        try {
            logOutput("connecting to " + peer.getIp() + " at port " + peer.getPort());
            Socket socket = new Socket(peer.getIp(), peer.getPort(), InetAddress.getLocalHost(), clientPort);
            connectionStates.put(peer, ConnectionState.getInitialState());
            // 1. send handshake
            byte[] handshakeMessage = MessageBuilder.buildHandshake(filename);
            socket.getOutputStream().write(handshakeMessage);
        } catch (IOException e) {
            e.printStackTrace();
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

    // TODO: do we need this if we're not implementing end-game behavior?
    public void sendCancel(Inet4Address peer) {
    }

    private void logOutput(String s) {
        System.out.println("Client " + clientName + ": " + s);
    }
}
