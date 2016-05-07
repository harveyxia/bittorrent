package core;

import message.Message;
import message.MessageParser;
import utils.Datafile;
import utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Schedules tasks that respond to received messages.
 */
public class Responder implements Runnable {

    private final Uploader uploader;
    private final Downloader downloader;
    private ConcurrentMap<Peer, Connection> connections;
    private ConcurrentHashMap<Peer, Float> unchokedPeers;
    private Datafile datafile;
    private ScheduledExecutorService executor;
    private Logger logger;

    public Responder(ConcurrentMap<Peer, Connection> connections,
                     ConcurrentHashMap<Peer, Float> unchokedPeers,
                     Datafile datafile,
                     ScheduledExecutorService executor,
                     Logger logger) {
        this.connections = connections;
        this.unchokedPeers = unchokedPeers;
        this.datafile = datafile;
        this.executor = executor;
        this.logger = logger;
        this.downloader = new Downloader(logger);
        this.uploader = new Uploader(logger, unchokedPeers);
    }

    @Override
    public void run() {

        while (true) {
            for (Map.Entry<Peer, Connection> connection : connections.entrySet()) {
                try {
                    InputStream input = connection.getValue().getSocket().getInputStream();
                    if (input.available() == 0) {
                        continue;
                    }
                    Message message = MessageParser.parseMessage(input);
                    //                    logger.log(message.getMessageID());
                    executor.submit(new RespondTask(downloader,
                            uploader,
                            connection.getValue(),
                            connection.getKey(),
                            connections,
                            message,
                            datafile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
