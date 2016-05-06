package core;

import message.Message;
import message.MessageParser;
import utils.Datafile;
import utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Schedules tasks that respond to received messages.
 */
public class Responder implements Runnable {

    private final Download download;
    private final Upload upload;
    private ConcurrentMap<Peer, Connection> connections;
    private Set<Peer> unchokedPeers;
    private Datafile datafile;
    private ScheduledExecutorService executor;
    private Logger logger;

    public Responder(ConcurrentMap<Peer, Connection> connections, Set<Peer> unchokedPeers, Datafile datafile, ScheduledExecutorService executor, Logger logger) {
        this.connections = connections;
        this.unchokedPeers = unchokedPeers;
        this.datafile = datafile;
        this.executor = executor;
        this.logger = logger;
        this.download = new Download(logger);
        this.upload = new Upload();
    }

    @Override
    public void run() {

        while (true) {
            for (Connection connection : connections.values()) {
                try {
                    InputStream input = connection.getSocket().getInputStream();
                    if (input.available() == 0) {
                        continue;
                    }
                    Message message = MessageParser.parseMessage(input);
                    logger.log(message.toString());
                    executor.submit(new RespondTask(download, upload, connection, connections, message, datafile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
