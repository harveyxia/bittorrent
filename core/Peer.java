package core;

import java.net.InetSocketAddress;

/**
 * Created by marvin on 5/2/16.
 */
public class Peer {

    private InetSocketAddress addr;

    public Peer(InetSocketAddress addr) {
        this.addr = addr;
    }

    public InetSocketAddress getAddr() {
        return addr;
    }
}
