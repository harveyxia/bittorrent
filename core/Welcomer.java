package core;

import message.Message;
import message.MessageParser;
import utils.Datafile;
import utils.Logger;
import utils.MessageSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

/**
 * Accepts new peer connections.
 */
public class Welcomer implements Runnable {

    private int port, backlog;
    private Logger logger;
    private Datafile datafile;
    private ConcurrentMap<Peer, Connection> connections;

    public Welcomer(int port, int backlog, ConcurrentMap<Peer, Connection> connections, Logger logger, Datafile datafile) {
        this.port = port;
        this.backlog = backlog;
        this.connections = connections;
        this.logger = logger;
        this.datafile = datafile;
    }

    @Override
    public void run() {

        try {
            ServerSocket socket = new ServerSocket(port, backlog);
            while (true) {
                // Accept peer connections
                try {
                    Socket peerSocket = socket.accept();
                    logger.log("accepted new connection from " + peerSocket.getInetAddress() + " at port " + peerSocket.getPort());

                    Message message = MessageParser.parseMessage(peerSocket.getInputStream());
                    if (message.getMessageID() != Message.MessageID.HANDSHAKE_ID) {
                        logger.log("no handshake, rejected connection from " + peerSocket.getInetAddress() + " at port " + peerSocket.getPort());
                        continue;   // reject any peers that don't handshake first
                    }

                    Peer peer = new Peer(message.getPeerIp(), message.getPeerPort());
                    connections.put(peer, Connection.getInitialState(peerSocket));

                    MessageSender.sendBitfield(connections.get(peer), peer, logger, datafile.getBitfield());
                    //                    byte[] bitfieldMessage = MessageBuilder.buildBitfield(datafile.getBitfield().getByteArray());
                    //                    MessageSender.sendMessage(peerSocket, bitfieldMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
