package tracker;

import core.Peer;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
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

    public TrackerResponse(byte[] msg) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(msg);
        DataInputStream is = new DataInputStream(bais);
        interval = is.readInt();
        seeders = is.readInt();
        leechers = is.readInt();
        peers = new ArrayList<>();
        byte[] raw = new byte[4];
        for (int i = 0; i < seeders + leechers; i++) {
            is.read(raw);
            InetAddress ip = InetAddress.getByAddress(raw);
            int port = is.readInt();
            peers.add(new Peer(ip, port));
        }
    }

    public byte[] pack() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeInt(interval);
        os.writeInt(seeders);
        os.writeInt(leechers);
        for (Peer p : peers) {
            os.write(p.getIp().getAddress());
            os.writeInt(p.getPort());
        }
        return baos.toByteArray();
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
