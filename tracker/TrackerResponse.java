package tracker;

import core.Peer;

import java.io.*;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the tracker response, which contains a ping interval and a peer list for the requested file.
 */
public class TrackerResponse {

    private int interval, seeders, leechers;
    private Set<Peer> peers;

    public TrackerResponse(int interval, int seeders, int leechers, Set<Peer> peers) {
        this.interval = interval;
        this.seeders = seeders;
        this.leechers = leechers;
        this.peers = peers;
    }

    public static TrackerResponse fromStream(InputStream in) throws IOException {

        DataInputStream dis = new DataInputStream(in);

        try {
            int interval = dis.readInt();
            int seeders = dis.readInt();
            int leechers = dis.readInt();
            Set<Peer> peers = new HashSet<>();
            byte[] raw = new byte[4];
            for (int i = 0; i < seeders + leechers; i++) {
                dis.read(raw);
                InetAddress ip = InetAddress.getByAddress(raw);
                int port = dis.readInt();
                peers.add(new Peer(ip, port));
            }
            return new TrackerResponse(interval, seeders, leechers, peers);
        } catch (Exception e) {
            // was a malformed request
            return null;
        }
    }

    public void send(OutputStream out) throws IOException {

        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(interval);
        dos.writeInt(seeders);
        dos.writeInt(leechers);

        if (peers == null)
            return;
        for (Peer p : peers) {
            dos.write(p.getIp().getAddress());
            dos.writeInt(p.getPort());
        }
    }

    public int getInterval() {
        return interval;
    }

    public int getSeeders() {
        return seeders;
    }

    public int getLeechers() {
        return leechers;
    }

    public Set<Peer> getPeers() {
        return peers;
    }
}
