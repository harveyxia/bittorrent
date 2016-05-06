package core;

import utils.Datafile;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Unchoke algorithm, tit-for-tat + optimistic unchoking.
 */
public class Unchoker implements Runnable {

    private final int UNCHOKE_SLOTS = 4;
    private int i;
    private ConcurrentMap<Peer, Connection> connections;
    private Datafile datafile;
    private Set<Peer> unchokedPeers;
    private Random random = new Random();

    public Unchoker(ConcurrentMap<Peer, Connection> connections, Datafile datafile, Set<Peer> unchokedPeers) {
        this.connections = connections;
        this.datafile = datafile;
        this.unchokedPeers = unchokedPeers;
        this.i = 0;
    }

    /**
     * 1. Calculate data-receiving rates from all peers.
     * 2. Take top 4 as unchoked.
     * 3. Every 3 instances of run(), choose 1 peer at random from remaining for optimistic unchoking.
     */
    @Override
    public void run() {
        HashMap<Peer, Float> rates = getRates();
        // Take 4 peers with top rates
        Map<Peer, Float> sortedRates = rates.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(4)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        unchokedPeers = sortedRates.keySet();
//        if (i == 0) {       // optimistic unchoking
//            unchokedPeers.add(getOptimisticUnchoke());
//        }
//        i = (i + 1) % 3;
    }

    private HashMap<Peer, Float> getRates() {
        HashMap<Peer, Float> rates = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        if (datafile.isCompleted()) {       // use upload rate to determine 4 unchoke slots
            for (Map.Entry<Peer, Connection> connection : connections.entrySet()) {
                if (connection.getValue().getUploadState().isInterested()) {
                    rates.put(connection.getKey(), connection.getValue().getUploadRate(currentTime));
                }
            }
        } else {                            // use download rate to determine 4 unchoke slots
            for (Map.Entry<Peer, Connection> connection : connections.entrySet()) {
                if (connection.getValue().getUploadState().isInterested()) {
                    rates.put(connection.getKey(), connection.getValue().getDownloadRate(currentTime));
                }
            }
        }
        return rates;
    }

//    private Peer getOptimisticUnchoke() {
//        Peer optimisticPeer = null;
//        int numPeers = connections.keySet().size();
//        int itemIndex = random.nextInt(numPeers);
//        for (Peer peer : connections.keySet()) {
//            if (i == itemIndex) {
//                if (peer )
//                optimisticPeer = peer;
//                break;
//            }
//            i++;
//        }
//        return optimisticPeer;
//    }
}
