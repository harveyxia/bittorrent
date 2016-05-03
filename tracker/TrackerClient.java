package tracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by marvin on 5/2/16.
 */
public class TrackerClient {

    private InetSocketAddress client, server;
    private String filename;

    public TrackerClient(InetSocketAddress client, InetSocketAddress server, String filename) {
        this.client = client;
        this.server = server;
        this.filename = filename;
    }

    public TrackerResponse update(TrackerRequest.Event event) throws IOException {

        try (Socket socket = new Socket()) {
            socket.bind(null);
            socket.connect(server);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            TrackerRequest req = new TrackerRequest(event, client, filename);
            req.send(out);
            return TrackerResponse.fromStream(in);
        }
    }
}
