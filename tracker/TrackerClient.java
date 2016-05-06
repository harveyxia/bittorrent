package tracker;

import utils.Datafile;

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
    private Datafile datafile;

    public TrackerClient(InetSocketAddress client, InetSocketAddress server, Datafile datafile) {
        this.client = client;
        this.server = server;
        this.datafile = datafile;
    }

    public TrackerResponse update(TrackerRequest.Event event) throws IOException {

        try (Socket socket = new Socket()) {
            socket.bind(null);
            socket.connect(server);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            TrackerRequest req = new TrackerRequest(event, client, datafile.getFilename());
            req.send(out);
            return TrackerResponse.fromStream(in);
        }
    }

    public InetSocketAddress getClient() {
        return client;
    }

    public InetSocketAddress getServer() {
        return server;
    }

    public Datafile getDatafile() {
        return datafile;
    }
}
