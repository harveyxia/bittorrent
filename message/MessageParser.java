package message;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Parses client to client messages.
 */
public class MessageParser {

    public static MessageBuilder.MessageId getMessageId(byte[] message) {
        byte[] messageId = new byte[MessageBuilder.intByteLength];
        System.arraycopy(message, 0, messageId, 0, MessageBuilder.intByteLength);
        return MessageBuilder.MessageId.values()[byteToInt(messageId)];
    }

    public static int parseHave(byte[] message) {
        byte[] pieceIndex = new byte[MessageBuilder.intByteLength];
        ByteArrayInputStream input = new ByteArrayInputStream(message);
        input.skip(MessageBuilder.intByteLength);
        input.read(pieceIndex, 0, MessageBuilder.intByteLength);
        return byteToInt(pieceIndex);
    }

    public static Request parseRequest(byte[] message) {
        ByteArrayInputStream input = new ByteArrayInputStream(message);
        byte[] pieceIndexArray = new byte[MessageBuilder.intByteLength];
        byte[] beginArray = new byte[MessageBuilder.intByteLength];
        byte[] lengthArray = new byte[MessageBuilder.intByteLength];

        input.skip(MessageBuilder.intByteLength);
        input.read(pieceIndexArray, 0, MessageBuilder.intByteLength);
        input.read(beginArray, 0, MessageBuilder.intByteLength);
        input.read(lengthArray, 0, MessageBuilder.intByteLength);

        int pieceIndex = byteToInt(pieceIndexArray);
        int begin = byteToInt(beginArray);
        int length = byteToInt(lengthArray);
        return new Request(pieceIndex, begin, length);
    }

    public static Piece parsePiece(byte[] message) {
        ByteArrayInputStream input = new ByteArrayInputStream(message);
        byte[] pieceIndexArray = new byte[MessageBuilder.intByteLength];
        byte[] beginArray = new byte[MessageBuilder.intByteLength];
        byte[] blockLengthArray = new byte[MessageBuilder.intByteLength];

        input.skip(MessageBuilder.intByteLength);
        input.read(pieceIndexArray, 0, MessageBuilder.intByteLength);
        input.read(beginArray, 0, MessageBuilder.intByteLength);
        input.read(blockLengthArray, 0, MessageBuilder.intByteLength);

        int pieceIndex = byteToInt(pieceIndexArray);
        int begin = byteToInt(beginArray);
        int blockLength = byteToInt(blockLengthArray);
        byte[] block = new byte[blockLength];
        input.read(block, 0, blockLength);
        return new Piece(pieceIndex, begin, block);
    }

    public static Bitfield parseBitfield(byte[] message) {
        ByteArrayInputStream input = new ByteArrayInputStream(message);
        byte[] bitfieldLengthArray = new byte[MessageBuilder.intByteLength];
        input.skip(MessageBuilder.intByteLength);
        input.read(bitfieldLengthArray, 0, MessageBuilder.intByteLength);
        int bitfieldLength = byteToInt(bitfieldLengthArray);

        byte[] bitfield = new byte[bitfieldLength];
        input.read(bitfield, 0, bitfieldLength);
        return new Bitfield(bitfield);
    }

    private static int byteToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    }
}
