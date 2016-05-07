package tracker;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Class representing a request from client to tracker and vice versa.
 */
public class TrackerRequest {

    private Event event;
    private InetSocketAddress addr;
    private String filename;

    public TrackerRequest(Event event, InetSocketAddress addr, String filename) {
        this.event = event;
        this.addr = addr;
        this.filename = filename;
    }

    public static TrackerRequest fromStream(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        Event event = Event.values()[dis.readShort()];
        byte[] raw = new byte[4];
        dis.read(raw);
        InetAddress ip = InetAddress.getByAddress(raw);
        int port = dis.readInt();
        InetSocketAddress addr = new InetSocketAddress(ip, port);
        int length = dis.readInt();
        byte[] fileRaw = new byte[length];

        dis.read(fileRaw, 0, length);
        String filename = new String(fileRaw, StandardCharsets.US_ASCII);
        return new TrackerRequest(event, addr, filename);
    }

    public void send(OutputStream out) throws IOException {

        DataOutputStream dos = new DataOutputStream(out);
        dos.writeShort(event.ordinal());
        dos.write(addr.getAddress().getAddress());
        dos.writeInt(addr.getPort());
        dos.writeInt(filename.length());
        dos.write(filename.getBytes(StandardCharsets.US_ASCII));
        dos.flush();
    }

    public Event getEvent() {
        return event;
    }

    public InetSocketAddress getAddr() {
        return addr;
    }

    public String getFilename() {
        return filename;
    }

    public enum Event {
        STARTED,
        STOPPED,
        COMPLETED,
        PING
    }
}
