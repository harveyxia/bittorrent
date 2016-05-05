package tracker;

import core.Peer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Bittorrent tracker.
 */
public class Tracker {

    private ServerSocket welcomeSocket;
    private HashMap<String,List<Peer>> peerLists;

    public Tracker(int port) throws IOException {
        this.welcomeSocket = new ServerSocket(port);
        this.peerLists = new HashMap<String,List<Peer>>();
    }

    public void listen() throws IOException {

        while (true) {
            try (Socket socket = welcomeSocket.accept()) {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                TrackerRequest req = TrackerRequest.fromStream(in);
                TrackerResponse resp = processReq(req); // TODO: should actually do something
                resp.send(out);
            }
        }
    }

    private TrackerResponse processReq(TrackerRequest req) {
        Event event = req.getEvent();
        InetSocketAddress addr = req.getAddr();
        String fileName = req.getFileName();

        switch (event) {
            case Event.COMPLETED:
                // must be submitting a new file
                if (!peerLists.containsKey(fileName)){
                    List<Peer> peers = new ArrayList<>();
                    peers.add(new Peer(addr.getAddress(), addr.getPort()));
                    peerLists.put(fileName, peers);
                    return new TrackerResponse(2, 1, 0, peers);
                }

                // else do nothing, not tracking # seeders / leachers
                break;

            case Event.STARTED:
                // starting a new session but file doesn't exist
                if (!peerLists.containsKey(fileName)) {
                    return new TrackerResponse(2, 0, 0, null);
                }

                // else new session for existing file
                // note that this only says it was tracked at SOME point
                // may no longer be seeded
                List<Peer> peers = peerLists.get(fileName);
                // TODO: is this mutable?
                peers.add(new Peer(addr.getAddress(), addr.getPort()));
                return new TrackerResponse(2, peers.length(), 0, peers);

            case Event.STOPPED:

                // stupid request
                if (!peerLists.containsKey(fileName)) {
                    break;
                }

                // else remove from peer list
                List<Peer> peers = peerLists.get(fileName);
                // TODO: is this mutable?
                peers.remove(new Peer(addr.getAddress(), addr.getPort()));
                break;

            default:

                // stupid request
                if (!peerLists.containsKey(fileName)) {
                    break;
                }

                // else just a regular annoucement
                List<Peer> peers = peerLists.get(fileName);
                return new TrackerResponse(2, peers.length(), 0, peers);
        }     

        // if it gets this far, then it's a malformed request
        return new TrackerResponse(-1, 0, 0, null);
    }
}
