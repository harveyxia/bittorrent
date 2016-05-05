package message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static message.Message.MessageID.values;

/**
 * Parses client to client messages.
 */
public class MessageParser {

    public static Message parseMessage(InputStream inputStream) {
        Message message = null;
        int messageIdInt = readIntFromStream(inputStream);
        Message.MessageID messageId = values()[messageIdInt];
        try {
            switch (messageId) {
                case CHOKE_ID:
                    message = new Message(messageId);
                    break;
                case UNCHOKE_ID:
                    message = new Message(messageId);
                    break;
                case INTERESTED_ID:
                    message = new Message(messageId);
                    break;
                case NOT_INTERESTED_ID:
                    message = new Message(messageId);
                    break;
                case HAVE_ID:
                    message = parseHave(inputStream);
                    break;
                case REQUEST_ID:
                    message = parseRequest(inputStream);
                    break;
                case PIECE_ID:
                    message = parsePiece(inputStream);
                    break;
                case BITFIELD_ID:
                    message = parseBitfield(inputStream);
                    break;
                case HANDSHAKE_ID:
                    message = parseHandshake(inputStream);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static Message parseHave(InputStream inputStream) throws IOException {
        int pieceIndex = readIntFromStream(inputStream);
        return new Message(Message.MessageID.HAVE_ID, pieceIndex);
    }

    public static Message parseHandshake(InputStream input) throws IOException {
        byte[] filenameLengthArray = new byte[MessageBuilder.intByteLength];
        input.read(filenameLengthArray, 0, MessageBuilder.intByteLength);
        int filenameLength = byteToInt(filenameLengthArray);
        byte[] filenameArray = new byte[filenameLength];
        input.read(filenameArray, 0, filenameLength);
        String filename = new String(filenameArray, StandardCharsets.UTF_8);
        return new Message(Message.MessageID.HANDSHAKE_ID, filename);
    }

    public static Message parseRequest(InputStream input) throws IOException {
        byte[] pieceIndexArray = new byte[MessageBuilder.intByteLength];
        byte[] beginArray = new byte[MessageBuilder.intByteLength];
        byte[] lengthArray = new byte[MessageBuilder.intByteLength];

        input.read(pieceIndexArray, 0, MessageBuilder.intByteLength);
        input.read(beginArray, 0, MessageBuilder.intByteLength);
        input.read(lengthArray, 0, MessageBuilder.intByteLength);

        int pieceIndex = byteToInt(pieceIndexArray);
        int begin = byteToInt(beginArray);
        int length = byteToInt(lengthArray);
        Request request = new Request(pieceIndex, begin, length);
        return new Message(Message.MessageID.REQUEST_ID, request);
    }

    public static Message parsePiece(InputStream input) throws IOException {
        byte[] pieceIndexArray = new byte[MessageBuilder.intByteLength];
        byte[] beginArray = new byte[MessageBuilder.intByteLength];
        byte[] blockLengthArray = new byte[MessageBuilder.intByteLength];

        input.read(pieceIndexArray, 0, MessageBuilder.intByteLength);
        input.read(beginArray, 0, MessageBuilder.intByteLength);
        input.read(blockLengthArray, 0, MessageBuilder.intByteLength);

        int pieceIndex = byteToInt(pieceIndexArray);
        int begin = byteToInt(beginArray);
        int blockLength = byteToInt(blockLengthArray);
        byte[] block = new byte[blockLength];
        input.read(block, 0, blockLength);
        Piece piece = new Piece(pieceIndex, begin, block);
        return new Message(Message.MessageID.PIECE_ID, piece);
    }

    public static Message parseBitfield(InputStream input) throws IOException {
        byte[] bitfieldLengthArray = new byte[MessageBuilder.intByteLength];
        input.read(bitfieldLengthArray, 0, MessageBuilder.intByteLength);
        int bitfieldLength = byteToInt(bitfieldLengthArray);

        byte[] bitfieldArray = new byte[bitfieldLength];
        input.read(bitfieldArray, 0, bitfieldLength);
        Bitfield bitfield = new Bitfield(bitfieldArray);
        return new Message(Message.MessageID.BITFIELD_ID, bitfield);
    }

    public static int byteToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    // Reads a 4-byte int from inputStream. Has the side effect of advancing the input stream.
    public static int readIntFromStream(InputStream inputStream) {
        byte[] i = new byte[MessageBuilder.intByteLength];
        try {
            inputStream.read(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MessageParser.byteToInt(i);
    }
}
