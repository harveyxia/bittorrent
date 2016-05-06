package message;

import java.net.InetAddress;

/**
 * Harvey Xia.
 */
public class Message {
    private MessageID messageID;
    private int pieceIndex;
    private String filename;
    private InetAddress peerIp;
    private int peerPort;
    private Request request;
    private Piece piece;
    private Bitfield bitfield;

    // CHOKE, UNCHOKE, INTERESTED, NOT_INTERESTED
    public Message(MessageID messageID) {
        this.messageID = messageID;
    }

    // HANDSHAKE
    public Message(MessageID messageID, String filename, InetAddress peerIp, int peerPort) {
        this.messageID = messageID;
        this.filename = filename;
        this.peerIp = peerIp;
        this.peerPort = peerPort;
    }

    // HAVE
    public Message(MessageID messageID, int pieceIndex) {
        this.messageID = messageID;
        this.pieceIndex = pieceIndex;
    }

    // BITFIELD
    public Message(MessageID messageID, Bitfield bitfield) {
        this.messageID = messageID;
        this.bitfield = bitfield;
    }

    // PIECE
    public Message(MessageID messageID, Piece piece) {
        this.messageID = messageID;
        this.piece = piece;
    }

    // REQUEST
    public Message(MessageID messageID, Request request) {
        this.messageID = messageID;
        this.request = request;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public String getFilename() {
        return filename;
    }

    public Request getRequest() {
        return request;
    }

    public Piece getPiece() {
        return piece;
    }

    public Bitfield getBitfield() {
        return bitfield;
    }

    public MessageID getMessageID() {
        return messageID;
    }

    public InetAddress getPeerIp() {
        return peerIp;
    }

    public int getPeerPort() {
        return peerPort;
    }

    public enum MessageID {
        INTERESTED_ID, NOT_INTERESTED_ID, HAVE_ID, REQUEST_ID, PIECE_ID, BITFIELD_ID, HANDSHAKE_ID, CHOKE_ID, UNCHOKE_ID
    }
}
