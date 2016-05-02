package utils;

import java.nio.charset.StandardCharsets;

/**
 * Build torrent messages.
 */
public class MessageBuilder {

    public static String buildChoke() {
        return "choke:";
    }

    public static String buildInterested() {
        return "interested:";
    }

    public static String buildNotInterested() {
        return "not interested:";
    }


    /**
     * @param pieceIndex zero-based index of a piece that is downloaded and verified
     */
    public static String buildHave(int pieceIndex) {
        return "have:" + Integer.toString(pieceIndex);
    }


    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param length     Integer specifying requested length
     */
    public static String buildRequest(int pieceIndex, int begin, int length) {
        return String.format("request:%d,%d,%d", pieceIndex, begin, length);
    }

    /**
     * @param pieceIndex Zero-based piece index
     * @param begin      Zero-based byte offset within piece
     * @param block      The data
     */
    public static String buildPiece(int pieceIndex, int begin, byte[] block) {
        String blockString = new String(block, StandardCharsets.UTF_8);
        return String.format("piece:%d,%d,%s", pieceIndex, begin, blockString);
    }

    /**
     * @param bitfield Byte array representing bitfield
     */
    public static String buildBitfield(byte[] bitfield) {
        String bitfieldString = new String(bitfield, StandardCharsets.UTF_8);
        return String.format("bitfield:%s", bitfieldString);
    }

}
