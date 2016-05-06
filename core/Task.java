package core;

import message.Message;

/**
 * Created by marvin on 5/5/16.
 */
public class Task implements Runnable {

    private ConnectionState connectionState;
    private Message message;

    public Task(ConnectionState connectionState, Message message) {
        this.connectionState = connectionState;
        this.message = message;
    }

    @Override
    public void run() {
        // TODO actually handle task
    }
}
