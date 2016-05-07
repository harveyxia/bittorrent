package core;

import metafile.MetaFile;
import tracker.TrackerClient;
import tracker.TrackerRequest;
import tracker.TrackerResponse;
import tracker.TrackerTask;
import utils.Datafile;
import utils.Logger;
import utils.MessageSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Executable for the client.
 */
public class Client {

    private static final int NUM_THREADS = 8;
    private static final int BACKLOG = 10;
    private static final String CMD_USAGE = "java Client name port metafile directory [registerFile]";
    private static final int UNCHOKE_INTERVAL = 5; // TODO: CHANGE TO 10

    public static void main(String[] args) throws IOException {
        boolean registerFile = false;
        if (args.length < 4 || args.length > 5) {
            System.out.println(args.length);
            System.out.println(CMD_USAGE);
            return;
        }

        if (args.length == 5) {
            registerFile = true;
        }

        Logger logger = new Logger(args[0]);
        int port = Integer.parseInt(args[1]);
        MetaFile metaFile = MetaFile.parseMetafile(args[2]);
        String directory = args[3];
        boolean createEmptyFile = !registerFile;        // if not registering new file
        Datafile datafile = new Datafile(
                createEmptyFile,
                metaFile.getInfo().getFilename(),
                directory,
                metaFile.getInfo().getFileLength(),
                metaFile.getInfo().getPieceLength());

        ConcurrentMap<Peer, Connection> connections = new ConcurrentHashMap<>();
        ConcurrentHashMap<Peer, Float> unchokedPeers = new ConcurrentHashMap<>();

        // probably shouldn't be local host if running on zoo or something
        InetSocketAddress client = new InetSocketAddress(InetAddress.getLocalHost(), port);
        TrackerClient trackerClient = new TrackerClient(client, metaFile.getAnnounce(), datafile);

        TrackerResponse initResponse = getInitialTrackerResponse(trackerClient, registerFile, connections, trackerClient, logger);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(NUM_THREADS);
        executor.scheduleAtFixedRate(new Unchoker(connections, datafile, unchokedPeers, logger), 0, UNCHOKE_INTERVAL, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new TrackerTask(trackerClient, connections, executor, logger),
                initResponse.getInterval(), initResponse.getInterval(), TimeUnit.SECONDS);
        executor.submit(new Welcomer(port, BACKLOG, connections, logger, datafile));
        executor.submit(new Responder(connections, unchokedPeers, datafile, executor, logger));
    }

    private static TrackerResponse getInitialTrackerResponse(TrackerClient trackerClient,
                                                             boolean registerFile,
                                                             ConcurrentMap<Peer, Connection> connections,
                                                             TrackerClient client,
                                                             Logger logger) {
        TrackerResponse response = null;
        try {
            if (registerFile) {
                response = trackerClient.update(TrackerRequest.Event.COMPLETED);
            } else {
                response = trackerClient.update(TrackerRequest.Event.STARTED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add new peers
        Set<Peer> peers = response.getPeers();
        for (Peer peer : peers) {
            if (!isPeerEqualToMe(peer, client)) {
                logger.log("Initializing connection to " + peer);
                connect(peer, trackerClient, connections, logger);
            }
        }
        return response;
    }

    // check if peer's IP and port equal this client's IP and port
    private static boolean isPeerEqualToMe(Peer peer, TrackerClient trackerClient) {
        return peer.getIp().equals(trackerClient.getClient().getAddress())
                && peer.getPort() == trackerClient.getClient().getPort();
    }

    private static void connect(Peer peer, TrackerClient trackerClient, ConcurrentMap<Peer, Connection> connections, Logger logger) {
        try {
            logger.log("connecting to " + peer.getIp() + " at port " + peer.getPort());

            Socket socket = new Socket(peer.getIp(), peer.getPort());

            Connection connection = Connection.getInitialState(socket);
            connections.put(peer, connection);
            MessageSender.sendHandshake(connection, peer, logger, trackerClient.getDatafile(), trackerClient);
            MessageSender.sendBitfield(connection, peer, logger, trackerClient.getDatafile().getBitfield());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
