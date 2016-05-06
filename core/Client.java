package core;

import metafile.MetaFile;
import tracker.TrackerClient;
import tracker.TrackerTask;
import utils.DataFile;
import utils.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * Executable for the client.
 */
public class Client {

    private static final int NUM_THREADS = 8;
    private static final int BACKLOG = 10;
    private static final String CMD_USAGE = "java Client name port metafile directory";

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.out.println(CMD_USAGE);
            return;
        }
        Logger logger = new Logger(args[0]);
        int port = Integer.parseInt(args[1]);
        MetaFile metaFile = MetaFile.parseMetafile(args[2]);
        DataFile datafile = new DataFile(false,
                metaFile.getInfo().getFilename(),
                args[3],
                metaFile.getInfo().getFileLength(),
                metaFile.getInfo().getPieceLength());
        ConcurrentMap<Peer, Connection> connections = new ConcurrentHashMap<>();
        // probably shouldn't be local host if running on zoo or something
        InetSocketAddress client = new InetSocketAddress(InetAddress.getLocalHost(), port);
        TrackerClient trackerClient = new TrackerClient(client, metaFile.getAnnounce(), datafile);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(NUM_THREADS);
        executor.submit(new TrackerTask(trackerClient, connections, executor, logger));
        executor.submit(new Welcomer(port, BACKLOG, connections, logger));
        executor.submit(new Responder(connections, datafile, executor, logger));
    }
}

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
