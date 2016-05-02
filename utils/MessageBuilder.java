package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Build torrent messages.
 * Encoding: message id : [int fields] : [length in bytes of data : data]
 * The optional fields are only for Piece and Bitfield messages
 */
public class MessageBuilder {

    public static final int intByteLength = 4; // number of bytes per int

    public enum MessageId {
        CHOKE_ID, INTERESTED_ID, NOT_INTERESTED_ID, HAVE_ID, REQUEST_ID, PIECE_ID, BITFIELD_ID
    }

    public static byte[] buildChoke() {
        return intToByte(MessageId.CHOKE_ID.ordinal());
    }

    public static byte[] buildInterested() {
        return intToByte(MessageId.INTERESTED_ID.ordinal());
    }

    public static byte[] buildNotInterested() {
        return intToByte(MessageId.NOT_INTERESTED_ID.ordinal());
    }


    /**
     * @param pieceIndex zero-based index of a piece that is downloaded and verified
     */
    public static byte[] buildHave(int pieceIndex) {
        byte[] message = new byte[2 * intByteLength];
        System.arraycopy(intToByte(MessageId.HAVE_ID.ordinal()), 0, message, 0, intByteLength);
        System.arraycopy(intToByte(pieceIndex), 0, message, intByteLength, intByteLength);
        return message;
    }


    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param length     Integer specifying requested length
     */
    public static byte[] buildRequest(int pieceIndex, int begin, int length) {
        byte[] message = new byte[4 * intByteLength];
        System.arraycopy(intToByte(MessageId.REQUEST_ID.ordinal()), 0, message, 0, intByteLength);
        System.arraycopy(intToByte(pieceIndex), 0, message, intByteLength, intByteLength);
        System.arraycopy(intToByte(begin), 0, message, intByteLength * 2, intByteLength);
        System.arraycopy(intToByte(length), 0, message, intByteLength * 3, intByteLength);
        return message;
    }

    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param block      The data
     */
    public static byte[] buildPiece(int pieceIndex, int begin, byte[] block) {
        byte[] message = new byte[4 * intByteLength + block.length];

        System.arraycopy(intToByte(MessageId.PIECE_ID.ordinal()), 0, message, 0, intByteLength);
        System.arraycopy(intToByte(pieceIndex), 0, message, intByteLength, intByteLength);
        System.arraycopy(intToByte(begin), 0, message, intByteLength * 2, intByteLength);
        System.arraycopy(intToByte(block.length), 0, message, intByteLength * 3, intByteLength);
        System.arraycopy(block, 0, message, intByteLength * 4, block.length);
        return message;
    }

    /**
     * @param bitfield Byte array representing bitfield
     */
    public static byte[] buildBitfield(byte[] bitfield) {
        byte[] message = new byte[2 * intByteLength + bitfield.length];
        System.arraycopy(intToByte(MessageId.BITFIELD_ID.ordinal()), 0, message, 0, intByteLength);
        System.arraycopy(intToByte(bitfield.length), 0, message, intByteLength, intByteLength);
        System.arraycopy(bitfield, 0, message, intByteLength * 2, bitfield.length);
        return message;
    }

    public static byte[] intToByte(int i) {
        ByteBuffer b = ByteBuffer.allocate(intByteLength);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(i);
        return b.array();
    }

}
