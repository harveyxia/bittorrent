package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        try {
            message.write(intToByte(MessageId.HAVE_ID.ordinal()));
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
            message.write(intToByte(MessageId.REQUEST_ID.ordinal()));
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
            message.write(intToByte(MessageId.PIECE_ID.ordinal()));
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
            message.write(intToByte(MessageId.BITFIELD_ID.ordinal()));
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

}
