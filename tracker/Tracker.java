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
import java.net.SocketTimeoutException;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bittorrent tracker.
 */
public class Tracker implements Runnable {


    private ServerSocket welcomeSocket;
    private ConcurrentHashMap<String,Set<Peer>> peerLists;
    private ConcurrentHashMap<String,ConcurrentHashMap<Peer, Timer>> timerList;

    private final static int TIMEOUT = 2; // timeout in seconds
    private boolean run;

    public Tracker(int port) throws IOException {
        this.welcomeSocket = new ServerSocket(port);
        this.welcomeSocket.setSoTimeout(1000);

        this.peerLists = new ConcurrentHashMap<>();
        this.timerList = new ConcurrentHashMap<>();
        this.run = true;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("java Tracker trackerPort");
            return;
        }
        int trackerPort = Integer.parseInt(args[0]);
        Tracker tracker = new Tracker(trackerPort);
        new Thread(tracker).start();
    }

    public void run() {
        while (run) {
            try (Socket socket = welcomeSocket.accept()) {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                TrackerRequest req = TrackerRequest.fromStream(in);
                System.out.println("Accepted new connection from " + req.getAddr());
                TrackerResponse resp = processReq(req);
                if (resp != null)
                    resp.send(out);
            } catch (SocketTimeoutException e){
                // ignore
                // used to retest run condition
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            welcomeSocket.close();
        } catch (Exception e) {
            // ignore
        }
    }

    public void shutdown(){
        run = false;
    }

    private TrackerResponse processReq(TrackerRequest req) {
        Event event = req.getEvent();
        InetSocketAddress addr = req.getAddr();
        String fileName = req.getFilename();
        Peer peer = new Peer(addr.getAddress(), addr.getPort());
        Set<Peer> peers;

        switch (event) {
            case COMPLETED:
                System.out.println("Completed: submitting file " + fileName);
                // must be submitting a new file
                if (!peerLists.containsKey(fileName)) {
                    peers = new HashSet<>();
                    peers.add(peer);
                    peerLists.put(fileName, peers);
                    startTimer(fileName, peer);
                    return new TrackerResponse(TIMEOUT, 1, 0, peers);
                }

                // finished downloading old file
                // so no need to respond
                return null;

            case STARTED:
                System.out.println("started");
                // starting a new session but file doesn't exist

                if (!peerLists.containsKey(fileName)) {
                    return new TrackerResponse(TIMEOUT, 0, 0, null);
                }

                // else new session for existing file
                // note that this only says it was tracked at SOME point
                // may no longer be seeded
                peers = peerLists.get(fileName);

                if (!peers.contains(peer)) {
                    peers.add(peer);
                    peerLists.put(fileName, peers);
                    startTimer(fileName, peer);
                }
                
                return new TrackerResponse(TIMEOUT, peers.size(), 0, peers);

            case STOPPED:
                System.out.println("stopped");
                // stupid request
                if (!peerLists.containsKey(fileName)) {
                    break;
                }

                // else remove from peer list
                peers = peerLists.get(fileName);
                if (peers.contains(peer)) {
                    peers.remove(peer);
                    peerLists.put(fileName, peers);
                    stopTimer(fileName, peer);
                }
                return null;

            default:

                // stupid request
                if (!peerLists.containsKey(fileName)) {
                    break;
                }

                // else just a regular annoucement
                peers = peerLists.get(fileName);
                if (!peers.contains(peer)) {
                    peers.add(peer);
                    peerLists.put(fileName, peers);
                }
                startTimer(fileName, peer);
                return new TrackerResponse(TIMEOUT, peers.size(), 0, peers);
        }

        // if it gets this far, then it's either a malformed request
        // or it's just an ACK
        return new TrackerResponse(-1, 0, 0, null);
    }

    public void stopTimer(String fileName, Peer peer) {
        ConcurrentHashMap<Peer, Timer> timers;
        if (timerList.containsKey(fileName)) {
            timers = timerList.get(fileName);
            if (timers.containsKey(peer)) {
                Timer t = timers.remove(peer);
                t.cancel();
                timerList.put(fileName, timers);
            }
        }
    }

    public void startTimer(String fileName, Peer peer) {
        ConcurrentHashMap<Peer, Timer> timers;

        stopTimer(fileName, peer);
        if (!timerList.containsKey(fileName)) {
            timerList.put(fileName, new ConcurrentHashMap<>());
        }

        timers = timerList.get(fileName);
        Timer timer = new Timer();
        // TODO: concurrency issues.
        timers.put(peer, timer);
        timerList.put(fileName, timers);
        timer.schedule(new CheckTimeout(this, fileName, peer, timer), TIMEOUT * 1000);
    }

    public class CheckTimeout extends TimerTask {
        private String fileName;
        private Peer peer;
        private Timer timer;
        private Tracker tracker;

        public CheckTimeout(Tracker tracker, String fileName, Peer peer, Timer timer) {
            this.tracker = tracker;
            this.fileName = fileName;
            this.peer = peer;
            this.timer = timer;
        }

        public void run() {
            if (peerLists.containsKey(fileName)) {
                Set<Peer> peers = peerLists.get(fileName);
                if (peers.contains(peer)) {
                    peers.remove(peer);
                    peerLists.put(fileName, peers);
                }
                timer.cancel();
            }
        }
    }
}
