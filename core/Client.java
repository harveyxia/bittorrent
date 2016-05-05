package core;

import message.Message;
import message.MessageBuilder;
import message.MessageParser;
import metafile.Metafile;
import metafile.MetafileUtils;
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
    private static final String CMD_USAGE = "java Client clientName metaFile clientPort serverPort";
    private static final int CLIENT_PORT = 200;
    private static final int SERVER_PORT = 300;
    private static Inet4Address trackerAddr;
    private static int trackerPort;

    private HashMap<Peer, ConnectionState> connectionStates;    // maintain bittorrent state of each p2p connection
    private HashMap<Peer, Socket> connections;                  // maintain TCP state of each p2p connection
    //    private HashMap<String, DataFile> files;                    // maintain map of filenames to files
    private HashSet<Peer> peers;                                // list of all peers
    private int clientPort;
    private int listenPort;
    private String clientName;

    private DataFile dataFile;

    public Client(String clientName, int clientPort, int listenPort, DataFile dataFile) {
        this.connectionStates = new HashMap<>();
        this.connections = new HashMap<>();
        this.clientName = clientName;
        this.clientPort = clientPort;
        this.listenPort = listenPort;
        this.dataFile = dataFile;
        logOutput("listening on port " + listenPort);
        logOutput("downloading on port " + clientPort);
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println(CMD_USAGE);
            return;
        }
        String metafileName = args[1];
        int clientPort = Integer.parseInt(args[2]);
        int listenPort = Integer.parseInt(args[3]);
        Metafile metafile = MetafileUtils.parseMetafile(metafileName);

        try {
            DataFile datafile = new DataFile(metafile.getInfo().getFilename(),
                    metafile.getInfo().getFileLength(),
                    metafile.getInfo().getPieceLength());
            Client client = new Client(args[0], clientPort, listenPort, datafile);
            Thread listenThread = client.getListenThread();
            Thread downloadThread = client.getEventThread("fileId", "trackerUrl");
            ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS);
            service.submit(listenThread);
            service.submit(downloadThread);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
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
                            connectionStates.put(peer, ConnectionState.getInitialState());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //                        // Process any new messages
                        //                        for (Peer peer : peers) {
                        //                            ConnectionState state = connectionStates.get(peer);
                        //                            if (state == null) continue;
                        //
                        //                            if (state.isEstablished()) {        // Piece exchange
                        //                                // Piece exchange
                        //                            } else {                            // Handshake
                        //                                // 1. Receive connectToPeer message
                        //                                // 2. Send connectToPeer message
                        //                            }
                        //                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverThread;
    }

    /**
     * Process messages from peers and take proper action.
     */
    public Thread getEventThread(String fileId, String trackerUrl) {
        Thread downloadThread;
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 1. contact tracker for initial peer list
                // 2. select peers to connectToPeer
                // 3. connectToPeer peers
                // 4. start event loop for each out going connection
                while (true) {
                    for (Peer peer : connectionStates.keySet()) {
                        try {
                            Socket peerSocket = connections.get(peer);
                            Message message = MessageParser.parseMessage(peerSocket.getInputStream());
                            // respond to message
                            // send update to peer about client's state towards the peer if changed
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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


    /**
     * Initiate connection to another peer.
     */
    public void connectToPeer(Peer peer, String filename) {
        try {
            logOutput("connecting to " + peer.getIp() + " at port " + peer.getPort());
            Socket socket = new Socket(peer.getIp(), peer.getPort(), InetAddress.getLocalHost(), clientPort);
            connections.put(peer, socket);
            connectionStates.put(peer, ConnectionState.getInitialState());
            sendHandshake(peer, filename);
            sendBitfield(peer, dataFile.getBitfield());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Requests from peer the first missing piece that peer has.
     */
    public void requestFirstAvailPiece(Peer peer) {
        ConnectionState peerState = connectionStates.get(peer);
        for (int i = 0; i < dataFile.getNumPieces(); i++) {
            if (dataFile.missingPiece(i) && peerState.getBitfield()[i] == 1) {
                sendRequest(peer, i, 0, dataFile.getPieceLength()); // request entire piece
                break;
            }
        }
    }

    public void sendRequest(Peer peer, int pieceIndex, int begin, int length) {
        logOutput(String.format("sending Request to " + peer + " for PieceIndex:%d,Begin:%d,Length:%d", pieceIndex, begin, length));
        dataFile.setPieceToRequested(pieceIndex);
        byte[] requestMessage = MessageBuilder.buildRequest(pieceIndex, begin, length);
        sendMessage(peer, requestMessage);
    }

    public void sendHave(Peer peer) {
    }

    public void sendChoke(Peer peer) {
    }

    public void sendUnchoke(Peer peer) {
    }

    public void sendInterested(Peer peer) {
    }

    public void sendUninterested(Peer peer) {
    }

    public void sendPiece(Peer peer, DataFile file, int index, int begin, int length) {
    }

    public void sendHandshake(Peer peer, String filename) {
        byte[] handshakeMessage = MessageBuilder.buildHandshake(filename);
        sendMessage(peer, handshakeMessage);
    }

    public void sendBitfield(Peer peer, byte[] bitfield) {
        byte[] bitfieldMessage = MessageBuilder.buildBitfield(bitfield);
        sendMessage(peer, bitfieldMessage);
    }

    public void sendMessage(Peer peer, byte[] message) {
        try {
            Socket peerSocket = connections.get(peer);
            peerSocket.getOutputStream().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logOutput(String s) {
        System.out.println("Client " + clientName + ": " + s);
    }
}
