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

    public String toString() {
        return ip + ":" + port;
    }

    public boolean equals(Object o) {
        //        if (o == this)
        //            return true;
        //        if (o == null)
        //            return false;

        //        if (getClass() != o.getClass())
        //            return false;

        Peer p = (Peer) o;
        return (port == p.getPort() && ip.equals(p.getIp()));
    }

    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + port;
        hash = hash * 31 + ip.hashCode();
        return hash;
    }


}
