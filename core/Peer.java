package core;

import java.net.InetAddress;

/**
 * Object wrapper for (InetAddress, port) pair.
 */
public class Peer {

    private InetAddress ip;
    private int port;

    public Peer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
