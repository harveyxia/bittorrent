package tracker;

import core.Connection;
import core.Peer;
import utils.Logger;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by marvin on 5/5/16.
 */
public class TrackerTask implements Runnable {

    private TrackerClient trackerClient;
    private ConcurrentMap<Peer, Connection> connections;
    private ScheduledExecutorService executor;
    private Logger logger;

    public TrackerTask(TrackerClient trackerClient, ConcurrentMap<Peer, Connection> connections, ScheduledExecutorService executor, Logger logger) {
        this.trackerClient = trackerClient;
        this.connections = connections;
        this.executor = executor;
        this.logger = logger;
    }

    @Override
    public void run() {

        try {
            TrackerResponse response = trackerClient.update(TrackerRequest.Event.PING); // change event later
            logger.log("Ping client");
            Set<Peer> peers = response.getPeers();
            // Remove no longer available peers
            for (Peer peer : connections.keySet()) {
                if (!peers.contains(peer)) {
                    Connection connection = connections.get(peer);
                    connections.remove(peer);
                    // TODO do some other cleanup
                    connection.getSocket().close();
                }
            }
            //            // Add new peers
            //            for (Peer peer : peers) {
            //                System.out.println("TrackerTask " + peer);
            //                if (!connections.containsKey(peer) && !isPeerEqualToMe(peer)) {
            //                    connect(peer);
            //                }
            //            }
            //            executor.schedule(this, response.getInterval(), TimeUnit.SECONDS);
        } catch (IOException e) {
            //            logger.log(e.toString());
            e.printStackTrace();
        }
    }
}
