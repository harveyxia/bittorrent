package core;

import java.net.InetAddress;

/**
 * Created by marvin on 5/2/16.
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
