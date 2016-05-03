package core;

import message.MessageBuilder;
import utils.DataFile;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static message.MessageParser.getMessageId;

/**
 * Bittorrent client.
 */
public class Client {

    private static final int BACKLOG = 10;
    private static final String CMD_USAGE = "java Client clientName clientPort serverPort";
    public static final int NUM_THREADS = 2;

    private HashMap<Inet4Address, ConnectionState> connectionStates;    // maintain bittorrent state of each p2p connection
    private HashMap<Inet4Address, Socket> connections;                  // maintain TCP state of each p2p connection
    private HashSet<Inet4Address> peers;                                // list of all peers
    private int clientPort;
    private int listenPort;
    private String clientName;

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

    public Client(String clientName, int clientPort, int listenPort) {
        this.connectionStates = new HashMap<>();
        this.connections = new HashMap<>();
        this.clientName = clientName;
        this.clientPort = clientPort;
        this.listenPort = listenPort;
        logOutput("listening on port " + listenPort);
        logOutput("downloading on port " + clientPort);
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
                        try (Socket peer = socket.accept()) {
                            logOutput("accepted new connection from " + peer.getInetAddress() + " at port " + peer.getPort());
                            connections.put((Inet4Address) peer.getInetAddress(), peer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Process any new messages
                        for (Socket peer : connections.values()) {
                            // TODO: process messages
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
                // 2. connect to peers
                try {
                    logOutput("getDownloadThread thread running");
                    Inet4Address address = (Inet4Address) Inet4Address.getLocalHost();
                    int port = 7000;
                    connect(address, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
        return downloadThread;
    }

    public Inet4Address[] getPeers() {
        // TODO: get peer list from tracker
        return new Inet4Address[0];
    }

    public void connect(Inet4Address peer, int port) {
        try {
            logOutput("connecting to " + peer + " at port " + port);
            Socket socket = new Socket(peer, port, InetAddress.getLocalHost(), clientPort);
            connectionStates.put(peer, ConnectionState.getInitialState());
            // TODO: send desired filename
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

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

    private void logOutput(String s) {
        System.out.println("Client " + clientName + ": " + s);
    }
}
