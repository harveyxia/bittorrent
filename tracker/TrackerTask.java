package tracker;

import core.Connection;
import core.Peer;
import utils.Logger;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Client task that pings server and receives updated peer list.
 */
public class TrackerTask implements Runnable {

    private TrackerClient trackerClient;
    private String fileName;
    private ConcurrentMap<Peer, Connection> connections;
    private ScheduledExecutorService executor;
    private Logger logger;

    public TrackerTask(TrackerClient trackerClient,
                       String fileName,
                       ConcurrentMap<Peer, Connection> connections,
                       ScheduledExecutorService executor,
                       Logger logger) {
        this.trackerClient = trackerClient;
        this.fileName = fileName;
        this.connections = connections;
        this.executor = executor;
        this.logger = logger;
    }

    @Override
    public void run() {

        try {
            TrackerResponse response = trackerClient.update(TrackerRequest.Event.PING); // change event later
            logger.log("Ping tracker");
            Set<Peer> peers = response.getPeers();
            logger.log(peers.toString());

            // Remove no longer available peers
            for (Peer peer : connections.keySet()) {
                if (!peers.contains(peer)) {
                    Connection connection = connections.get(peer);
                    connections.remove(peer);
                    logger.log("Removing peer " + peer + " for file " + fileName);
                    connection.getSocket().close();
                }
            }
        } catch (IOException e) {
            //            logger.log(e.toString());
            e.printStackTrace();
        }
    }
}
