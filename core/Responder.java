package core;

import message.Message;
import message.MessageParser;
import utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by marvin on 5/5/16.
 */
public class Responder implements Runnable {

    private ConcurrentMap<Peer, Connection> connections;
    private ScheduledExecutorService executor;
    private Logger logger;

    public Responder(ConcurrentMap<Peer, Connection> connections, ScheduledExecutorService executor, Logger logger) {
        this.connections = connections;
        this.executor = executor;
        this.logger = logger;
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
                    executor.submit(new Task(connection, message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
