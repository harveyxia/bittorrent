package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Build torrent messages.
 * Encoding: message id : [int fields] : [length in bytes of data : data]
 * The optional fields are only for Piece and Bitfield messages
 */
public class MessageBuilder {

    public static final int intByteLength = 4; // number of bytes per int

    public static byte[] buildChoke() {
        return intToByte(Message.MessageID.CHOKE_ID.ordinal());
    }

    public static byte[] buildUnchoke() {
        return intToByte(Message.MessageID.UNCHOKE_ID.ordinal());
    }

    public static byte[] buildInterested() {
        return intToByte(Message.MessageID.INTERESTED_ID.ordinal());
    }

    public static byte[] buildNotInterested() {
        return intToByte(Message.MessageID.NOT_INTERESTED_ID.ordinal());
    }

    /**
     * @param pieceIndex zero-based index of a piece that is downloaded and verified
     */
    public static byte[] buildHave(int pieceIndex) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        try {
            message.write(intToByte(Message.MessageID.HAVE_ID.ordinal()));
            message.write(intToByte(pieceIndex));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toByteArray();
    }


    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param length     Integer specifying requested length
     */
    public static byte[] buildRequest(int pieceIndex, int begin, int length) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        try {
            message.write(intToByte(Message.MessageID.REQUEST_ID.ordinal()));
            message.write(intToByte(pieceIndex));
            message.write(intToByte(begin));
            message.write(intToByte(length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toByteArray();
    }

    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param block      The data
     */
    public static byte[] buildPiece(int pieceIndex, int begin, byte[] block) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        try {
            message.write(intToByte(Message.MessageID.PIECE_ID.ordinal()));
            message.write(intToByte(pieceIndex));
            message.write(intToByte(begin));
            message.write(intToByte(block.length));
            message.write(block);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toByteArray();
    }

    /**
     * @param bitfield Byte array representing bitfield
     */
    public static byte[] buildBitfield(byte[] bitfield) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        try {
            message.write(intToByte(Message.MessageID.BITFIELD_ID.ordinal()));
            message.write(intToByte(bitfield.length));
            message.write(bitfield);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toByteArray();
    }

    public static byte[] intToByte(int i) {
        ByteBuffer b = ByteBuffer.allocate(intByteLength);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(i);
        return b.array();
    }

    /**
     * Format: [HANDSHAKE_ID, PEER IP, PORT PORT, FILENAME LENGTH, FILENAME]
     *
     * @param filename The filename.
     */
    public static byte[] buildHandshake(String filename, InetSocketAddress peerSocketAddress) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        byte[] filenameBytes = filename.getBytes(StandardCharsets.UTF_8);
        try {
            message.write(intToByte(Message.MessageID.HANDSHAKE_ID.ordinal()));
            message.write(peerSocketAddress.getAddress().getAddress());
            message.write(intToByte(peerSocketAddress.getPort()));
            message.write(intToByte(filenameBytes.length));
            message.write(filenameBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toByteArray();
    }

}
