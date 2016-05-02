package tracker;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Created by marvin on 5/2/16.
 */
public class TrackerRequest {

    public enum Event {
        STARTED,
        STOPPED,
        COMPLETED
    }
    private Event event;
    private InetAddress ip;
    private int port;
    private String filename;

    public TrackerRequest(Event event, InetAddress ip, int port, String filename) {
        this.event = event;
        this.ip = ip;
        this.port = port;
        this.filename = filename;
    }

    public TrackerRequest(byte[] msg) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(msg);
        DataInputStream is = new DataInputStream(bais);
        event = Event.values()[is.readShort()];
        byte[] raw = new byte[4];
        is.read(raw);
        ip = InetAddress.getByAddress(raw);
        port = is.readInt();
        int length = is.readInt();
        byte[] fileRaw = new byte[length];
        is.read(fileRaw);
        filename = new String(fileRaw, StandardCharsets.US_ASCII);
    }

    public byte[] pack() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeShort(event.ordinal());
        os.write(ip.getAddress());
        os.writeInt(port);
        os.writeInt(filename.length());
        os.writeBytes(filename);
        return baos.toByteArray();
    }

    public Event getEvent() {
        return event;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getFilename() {
        return filename;
    }
}
