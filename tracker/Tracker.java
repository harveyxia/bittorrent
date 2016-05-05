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


import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Bittorrent tracker.
 */
public class Tracker {

    private ServerSocket welcomeSocket;
    private ConcurrentHashMap<String,List<Peer>> peerLists;
    private ConcurrentHashMap<String,ConcurrentHashMap<Peer, Timer>> timerList;

    private final static int TIMEOUT = 2; // timeout in seconds

    public Tracker(int port) throws IOException {
        this.welcomeSocket = new ServerSocket(port);
        this.peerLists = new ConcurrentHashMap<String,List<Peer>>();
        this.timerList = new ConcurrentHashMap<String,ConcurrentHashMap<Peer, Timer>>();
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
                    return new TrackerResponse(TIMEOUT, 1, 0, peers);
                }

                // else do nothing, not tracking # seeders / leachers
                break;

            case STARTED:
                // starting a new session but file doesn't exist
                if (!peerLists.containsKey(fileName)) {
                    return new TrackerResponse(TIMEOUT, 0, 0, null);
                }

                // else new session for existing file
                // note that this only says it was tracked at SOME point
                // may no longer be seeded
                peers = peerLists.get(fileName);
                // TODO: is this mutable?
                peers.add(new Peer(addr.getAddress(), addr.getPort()));
                return new TrackerResponse(TIMEOUT, peers.size(), 0, peers);

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
                return new TrackerResponse(TIMEOUT, peers.size(), 0, peers);
        }     

        // if it gets this far, then it's a malformed request
        return new TrackerResponse(-1, 0, 0, null);
    }

    public void startTimer(String fileName, Peer peer) {
        ConcurrentHashMap<Peer, Timer> timers;
        if (timerList.contains(fileName)){
            timers = timerList.get(fileName);
            if (timers.contains(peer)){
                // TODO: is this mutable?
                timers.remove(peer);
            }
        } else {
            timerList.put(fileName, new ConcurrentHashMap<Peer, Timer>());
        }

        timers = timerList.get(fileName);
        Timer timer = new Timer();
        // TODO: is this mutable?
        // TODO: concurrency issues.
        timers.put(peer, timer);
        timer.schedule(new CheckTimeout(fileName, peer), TIMEOUT * 1000);
    }

    public class CheckTimeout extends TimerTask {
        private String fileName;
        private Peer peer;

        public CheckTimeout(String fileName, Peer peer) {
            this.fileName = fileName;
            this.peer = peer;
        }

        public void run() {
            if (peerLists.contains(fileName)){
                List<Peer> peers = peerLists.get(fileName);
                if (peers.contains(peer)){
                    // TODO: is this mutable?
                    peers.remove(peer);
                }
            }
        }
    }
}
