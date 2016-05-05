package tracker;

import core.Peer;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marvin on 5/2/16.
 */
public class TrackerResponse {

    private int interval, seeders, leechers;
    private List<Peer> peers;

    public TrackerResponse(int interval, int seeders, int leechers, List<Peer> peers) {
        this.interval = interval;
        this.seeders = seeders;
        this.leechers = leechers;
        this.peers = peers;
    }

    public static TrackerResponse fromStream(InputStream in) throws IOException {

        DataInputStream dis = new DataInputStream(in);
        int interval = dis.readInt();
        int seeders = dis.readInt();
        int leechers = dis.readInt();
        List<Peer> peers = new ArrayList<>();
        byte[] raw = new byte[4];
        for (int i = 0; i < seeders + leechers; i++) {
            dis.read(raw);
            InetAddress ip = InetAddress.getByAddress(raw);
            int port = dis.readInt();
            peers.add(new Peer(ip, port));
        }
        return new TrackerResponse(interval, seeders, leechers, peers);
    }

    public void send(OutputStream out) throws IOException {

        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(interval);
        dos.writeInt(seeders);
        dos.writeInt(leechers);
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

    public List<Peer> getPeers() {
        return peers;
    }
}