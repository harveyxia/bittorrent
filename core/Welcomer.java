package core;

import utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by marvin on 5/5/16.
 */
public class Welcomer implements Runnable {

    private int port, backlog;
    private Logger logger;
    private ConcurrentMap<Peer, Connection> connections;

    public Welcomer(int port, int backlog, ConcurrentMap<Peer, Connection> connections, Logger logger) {
        this.port = port;
        this.backlog = backlog;
        this.connections = connections;
        this.logger = logger;
    }

    @Override
    public void run() {

        try {
            ServerSocket socket = new ServerSocket(port, backlog);
            while (true) {
                // Accept peer connections
                try {
                    Socket peerSocket = socket.accept();
                    logger.log("accepted new connection from " + peerSocket.getInetAddress() + " at port " + peerSocket.getPort());
                    Peer peer = new Peer(peerSocket.getInetAddress(), peerSocket.getPort());
                    connections.put(peer, Connection.getInitialState(peerSocket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
