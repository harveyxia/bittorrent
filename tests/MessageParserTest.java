package tests;

import message.*;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Harvey Xia.
 */
public class MessageParserTest {

    @Test
    public void testGetMessageId() throws Exception {
        byte[] bitfield = {0, 1, 0, 1};
        byte[] message = MessageBuilder.buildBitfield(bitfield);
        assertEquals(MessageBuilder.MessageId.BITFIELD_ID, MessageParser.getMessageId(message));
    }

    @Test
    public void testParseHave() throws Exception {
        byte[] requestMessage = MessageBuilder.buildHave(12);
        assertEquals(12, MessageParser.parseHave(requestMessage));
    }

    @Test
    public void testParseRequest() throws Exception {
        Request expectedRequest = new Request(1, 2, 3);
        byte[] requestMessage = MessageBuilder.buildRequest(1, 2, 3);
        Request request = MessageParser.parseRequest(requestMessage);
        assertEquals(expectedRequest.getPieceIndex(), request.getPieceIndex());
        assertEquals(expectedRequest.getBegin(), request.getBegin());
        assertEquals(expectedRequest.getLength(), request.getLength());
    }

    @Test
    public void testParsePiece() throws Exception {
        byte[] block = "hello world!".getBytes(StandardCharsets.UTF_8);
        Piece expectedPiece = new Piece(1, 2, block);
        byte[] requestMessage = MessageBuilder.buildPiece(1, 2, block);
        Piece piece = MessageParser.parsePiece(requestMessage);
        assertEquals(expectedPiece.getPieceIndex(), piece.getPieceIndex());
        assertEquals(expectedPiece.getBegin(), piece.getBegin());
        assertArrayEquals(expectedPiece.getBlock(), piece.getBlock());
    }

    @Test
    public void testParseBitfield() throws Exception {
        byte[] bitfieldArray = {1, 0, 1, 0, 1};
        Bitfield expectedBitfield = new Bitfield(bitfieldArray);
        byte[] requestMessage = MessageBuilder.buildBitfield(bitfieldArray);
        Bitfield bitfield = MessageParser.parseBitfield(requestMessage);
        assertArrayEquals(bitfield.getBitfield(), expectedBitfield.getBitfield());
    }
}
