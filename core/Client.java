package core;

import metafile.MetaFile;
import utils.DataFile;
import utils.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by marvin on 5/5/16.
 */
public class Client {

    private static final int NUM_THREADS = 8;
    private static final int BACKLOG = 10;
    private static final String CMD_USAGE = "java Client name port metafile directory";

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println(CMD_USAGE);
            return;
        }
        Logger logger = new Logger(args[0]);
        int port = Integer.parseInt(args[1]);
        MetaFile metaFile = MetaFile.parseMetafile(args[2]);
        try {
            DataFile dataFile = new DataFile(false,
                    metaFile.getInfo().getFilename(),
                    args[3],
                    metaFile.getInfo().getFileLength(),
                    metaFile.getInfo().getPieceLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConcurrentMap<Peer, Connection> connections = new ConcurrentHashMap<>();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(NUM_THREADS);
        executor.submit(new Welcomer(port, BACKLOG, connections, logger));
        executor.submit(new Responder(connections, executor, logger));
    }
}

//    public TrackerResponse getTrackerResponse(String filename) throws IOException {
//
//        Socket socket = new Socket(trackerAddr, trackerPort);
//
//        // Send tracker request
//        TrackerRequest request = new TrackerRequest(TrackerRequest.Event.STARTED, (InetSocketAddress) socket.getLocalSocketAddress(), filename);
//        request.send(socket.getOutputStream());
//
//        // Receive tracker response
//        return TrackerResponse.fromStream(socket.getInputStream());
//    }
//
//    /**
//     * Initiate connection to another peer.
//     */
//    public void connectToPeer(Peer peer, String filename) {
//        try {
//            logOutput("connecting to " + peer.getIp() + " at port " + peer.getPort());
//            Socket socket = new Socket(peer.getIp(), peer.getPort(), InetAddress.getLocalHost(), clientPort);
//            connectionStates.put(peer, Connection.getInitialState(socket));
//            sendHandshake(peer, filename);
//            sendBitfield(peer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Requests from peer the first missing piece that peer has.
//     */
//    public void requestFirstAvailPiece(Peer peer) {
//        Connection peerState = connectionStates.get(peer);
//        for (int i = 0; i < dataFile.getNumPieces(); i++) {
//            if (dataFile.missingPiece(i) && peerState.hasPiece(i)) {
//                sendRequest(peer, i, 0, dataFile.getPieceLength()); // request entire piece
//                break;
//            }
//        }
//    }
//
//    public void sendRequest(Peer peer, int pieceIndex, int begin, int length) {
//        logOutput(String.format("sending Request to " + peer + " for PieceIndex:%d,Begin:%d,Length:%d", pieceIndex, begin, length));
//        dataFile.setPieceToRequested(pieceIndex);
//        byte[] requestMessage = MessageBuilder.buildRequest(pieceIndex, begin, length);
//        sendMessage(peer, requestMessage);
//    }
//
//    public void sendHave(Peer peer) {
//    }
//
//    public void sendChoke(Peer peer) {
//    }
//
//    public void sendUnchoke(Peer peer) {
//    }
//
//    public void sendInterested(Peer peer) {
//    }
//
//    public void sendUninterested(Peer peer) {
//    }
//
//    public void sendPiece(Peer peer, DataFile file, int index, int begin, int length) {
//    }
//
//    public void sendHandshake(Peer peer, String filename) {
//        byte[] handshakeMessage = MessageBuilder.buildHandshake(filename);
//        sendMessage(peer, handshakeMessage);
//    }
//
//    public void sendBitfield(Peer peer) {
//        byte[] bitfieldMessage = MessageBuilder.buildBitfield(dataFile.getBitfield());
//        sendMessage(peer, bitfieldMessage);
//    }