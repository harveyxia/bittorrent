package tracker;

import core.Peer;
import tracker.TrackerRequest.Event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;


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
        String fileName = req.getFilename();
        List<Peer> peers = null;

        switch (event) {
            case COMPLETED:
                // must be submitting a new file
                if (!peerLists.containsKey(fileName)){
                    peers = new ArrayList<>();
                    peers.add(new Peer(addr.getAddress(), addr.getPort()));
                    peerLists.put(fileName, peers);
                    return new TrackerResponse(2, 1, 0, peers);
                }

                // else do nothing, not tracking # seeders / leachers
                break;

            case STARTED:
                // starting a new session but file doesn't exist
                if (!peerLists.containsKey(fileName)) {
                    return new TrackerResponse(2, 0, 0, null);
                }

                // else new session for existing file
                // note that this only says it was tracked at SOME point
                // may no longer be seeded
                peers = peerLists.get(fileName);
                // TODO: is this mutable?
                peers.add(new Peer(addr.getAddress(), addr.getPort()));
                return new TrackerResponse(2, peers.size(), 0, peers);

            case STOPPED:

                // stupid request
                if (!peerLists.containsKey(fileName)) {
                    break;
                }

                // else remove from peer list
                peers = peerLists.get(fileName);
                // TODO: is this mutable?
                peers.remove(new Peer(addr.getAddress(), addr.getPort()));
                break;

            default:

                // stupid request
                if (!peerLists.containsKey(fileName)) {
                    break;
                }

                // else just a regular annoucement
                peers = peerLists.get(fileName);
                return new TrackerResponse(2, peers.size(), 0, peers);
        }     

        // if it gets this far, then it's a malformed request
        return new TrackerResponse(-1, 0, 0, null);
    }
}
