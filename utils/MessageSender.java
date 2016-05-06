package utils;

import java.io.IOException;
import java.net.Socket;

/**
 * Abstraction for sending messages.
 */
public class MessageSender {

    public static void sendMessage(Socket peerSocket, byte[] message) {
        try {
            peerSocket.getOutputStream().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
