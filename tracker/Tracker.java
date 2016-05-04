package tracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Bittorrent tracker.
 */
public class Tracker {

    private ServerSocket welcomeSocket;

    public Tracker(int port) throws IOException {

        welcomeSocket = new ServerSocket(port);
    }

    public void listen() throws IOException {

        while (true) {
            try (Socket socket = welcomeSocket.accept()) {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                TrackerRequest req = TrackerRequest.fromStream(in);
                // process it
                TrackerResponse resp = null; // TODO: should actually do something
                resp.send(out);
            }
        }
    }
}
