package tests;

import message.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Harvey Xia.
 */
public class MessageParserTest {

    @Test
    public void testParseHave() throws Exception {
        byte[] requestMessage = MessageBuilder.buildHave(12);
        InputStream is = new ByteArrayInputStream(requestMessage);
        assertEquals(Message.MessageID.HAVE_ID.ordinal(), MessageParser.readIntFromStream(is));
        Message message = MessageParser.parseHave(is);
        assertEquals(12, message.getPieceIndex());
    }

    @Test
    public void testParseRequest() throws Exception {
        int pieceIndex = 1;
        Request expectedRequest = new Request(pieceIndex, 2, 3);
        byte[] requestMessage = MessageBuilder.buildRequest(pieceIndex, 2, 3);

        InputStream is = new ByteArrayInputStream(requestMessage);
        assertEquals(Message.MessageID.REQUEST_ID.ordinal(), MessageParser.readIntFromStream(is));
        Message message = MessageParser.parseRequest(is);

        assertEquals(expectedRequest.getPieceIndex(), message.getRequest().getPieceIndex());
        assertEquals(expectedRequest.getBegin(), message.getRequest().getBegin());
        assertEquals(expectedRequest.getLength(), message.getRequest().getLength());
    }

    @Test
    public void testParsePiece() throws Exception {
        byte[] block = "hello world!".getBytes(StandardCharsets.UTF_8);
        Piece expectedPiece = new Piece(1, 2, block);
        byte[] requestMessage = MessageBuilder.buildPiece(1, 2, block);

        InputStream is = new ByteArrayInputStream(requestMessage);
        assertEquals(Message.MessageID.PIECE_ID.ordinal(), MessageParser.readIntFromStream(is));
        Message message = MessageParser.parsePiece(is);

        assertEquals(expectedPiece.getPieceIndex(), message.getPiece().getPieceIndex());
        assertEquals(expectedPiece.getBegin(), message.getPiece().getBegin());
        assertArrayEquals(expectedPiece.getBlock(), message.getPiece().getBlock());
    }

    @Test
    public void testParseBitfield() throws Exception {
        byte[] bitfieldArray = {1, 0, 1, 0, 1};
        Bitfield expectedBitfield = new Bitfield(bitfieldArray);
        byte[] requestMessage = MessageBuilder.buildBitfield(bitfieldArray);

        InputStream is = new ByteArrayInputStream(requestMessage);
        assertEquals(Message.MessageID.BITFIELD_ID.ordinal(), MessageParser.readIntFromStream(is));
        Message message = MessageParser.parseBitfield(is);
        assertArrayEquals(expectedBitfield.getByteArray(), message.getBitfield().getByteArray());
    }

    @Test
    public void testParseHandshake() throws Exception {
        String filename = "filename.txt";
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 9999);
        byte[] requestMessage = MessageBuilder.buildHandshake(filename, inetSocketAddress);
        InputStream is = new ByteArrayInputStream(requestMessage);
        assertEquals(Message.MessageID.HANDSHAKE_ID.ordinal(), MessageParser.readIntFromStream(is));
        Message actual = MessageParser.parseHandshake(is);
        assertEquals(filename, actual.getFilename());
        assertEquals(inetSocketAddress.getAddress(), actual.getPeerIp());
        assertEquals(9999, actual.getPeerPort());
    }
}
