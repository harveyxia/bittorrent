package utils;

import java.nio.charset.StandardCharsets;

/**
 * Build torrent messages.
 */
public class MessageBuilder {

    public static final int CHOKE_ID = 0;
    public static final int INTERESTED_ID = 1;
    public static final int NOT_INTERESTED = 2;
    public static final int HAVE_ID = 3;
    public static final int REQUEST_ID = 4;
    public static final int PIECE_ID = 5;
    public static final int BITFIELD_ID = 6;

    public static String buildChoke() {
        return Integer.toString(CHOKE_ID);
    }

    public static String buildInterested() {
        return Integer.toString(INTERESTED_ID);
    }

    public static String buildNotInterested() {
        return Integer.toString(NOT_INTERESTED);
    }


    /**
     * @param pieceIndex zero-based index of a piece that is downloaded and verified
     */
    public static String buildHave(int pieceIndex) {
        return HAVE_ID + ":" + Integer.toString(pieceIndex);
    }


    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param length     Integer specifying requested length
     */
    public static String buildRequest(int pieceIndex, int begin, int length) {
        return String.format(REQUEST_ID + ":%d,%d,%d", pieceIndex, begin, length);
    }

    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param block      The data
     */
    public static String buildPiece(int pieceIndex, int begin, byte[] block) {
        String blockString = new String(block, StandardCharsets.UTF_8);
        return String.format(PIECE_ID + ":%d,%d,%s", pieceIndex, begin, blockString);
    }

    /**
     * @param bitfield Byte array representing bitfield
     */
    public static String buildBitfield(byte[] bitfield) {
        String bitfieldString = new String(bitfield, StandardCharsets.UTF_8);
        return String.format(BITFIELD_ID + ":%s", bitfieldString);
    }

}
