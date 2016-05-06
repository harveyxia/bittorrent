package core;

import message.Message;

/**
 * Created by marvin on 5/5/16.
 */
public class Task implements Runnable {

    private Connection connection;
    private Message message;

    public Task(Connection connection, Message message) {
        this.connection = connection;
        this.message = message;
    }

    @Override
    public void run() {
        // TODO actually handle task
    }
}
