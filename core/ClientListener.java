package core;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Server thread on the client.
 */
class ClientListener implements Runnable {

    private ServerSocket socket;
    private HashMap<Inet4Address, Socket> peers;

    public ClientListener(ServerSocket socket) {
        this.socket = socket;
        this.peers = new HashMap<>();
    }

    public void run() {

        while (true) {

            // Accept peer connections
            try (Socket peer = socket.accept()) {
                peers.put((Inet4Address) peer.getInetAddress(), peer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Process any new messages
            for (Socket peer : peers.values()) {
                // TODO: process messages
            }
        }
    }
}
