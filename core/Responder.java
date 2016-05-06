package core;

import message.Message;
import message.MessageParser;
import utils.DataFile;
import utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Schedules tasks that respond to received messages.
 */
public class Responder implements Runnable {

    private final Downloader downloader;
    private ConcurrentMap<Peer, Connection> connections;
    private DataFile datafile;
    private ScheduledExecutorService executor;
    private Logger logger;

    public Responder(ConcurrentMap<Peer, Connection> connections, DataFile datafile, ScheduledExecutorService executor, Logger logger) {
        this.connections = connections;
        this.datafile = datafile;
        this.executor = executor;
        this.logger = logger;
        this.downloader = new Downloader(logger);
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
                    executor.submit(new RespondTask(downloader, connection, connections, message, datafile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
