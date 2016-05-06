package tracker;

import core.Connection;
import core.Peer;
import message.MessageBuilder;
import utils.Datafile;
import utils.Logger;
import utils.MessageSender;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
            TrackerResponse response = trackerClient.update(TrackerRequest.Event.STARTED); // change event later
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
            // Add new peers
            for (Peer peer : peers) {
                if (!connections.containsKey(peer)) {
                    connect(peer);
                }
            }
            executor.schedule(this, response.getInterval(), TimeUnit.SECONDS);
        } catch (IOException e) {
            logger.log(e.toString());
        }
    }

    private void connect(Peer peer) {

        try {
            logger.log("connecting to " + peer.getIp() + " at port " + peer.getPort());
            InetSocketAddress client = trackerClient.getClient();
            Socket socket = new Socket(peer.getIp(), peer.getPort(), client.getAddress(), client.getPort());
            Connection connection = Connection.getInitialState(socket);
            connections.put(peer, connection);
            sendHandshake(connection, trackerClient.getDatafile());
            sendBitfield(connection, trackerClient.getDatafile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHandshake(Connection connection, Datafile datafile) {
        byte[] handshakeMessage = MessageBuilder.buildHandshake(datafile.getFilename());
        MessageSender.sendMessage(connection.getSocket(), handshakeMessage);
    }

    private void sendBitfield(Connection connection, Datafile datafile) {
        byte[] bitfieldMessage = MessageBuilder.buildBitfield(datafile.getBitfield().getByteArray());
        MessageSender.sendMessage(connection.getSocket(), bitfieldMessage);
    }
}
