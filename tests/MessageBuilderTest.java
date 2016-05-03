package tests;

import message.Message;
import org.junit.Test;
import message.MessageBuilder;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static message.MessageBuilder.intToByte;

/**
 * Harvey Xia.
 */
public class MessageBuilderTest {

    @Test
    public void testBuildChoke() throws Exception {
        assertArrayEquals(intToByte(Message.MessageID.CHOKE_ID.ordinal()), MessageBuilder.buildChoke());
    }

    @Test
    public void testBuildInterested() throws Exception {
        assertArrayEquals(intToByte(Message.MessageID.INTERESTED_ID.ordinal()), MessageBuilder.buildInterested());
    }

    @Test
    public void testBuildNotInterested() throws Exception {
        assertArrayEquals(intToByte(Message.MessageID.NOT_INTERESTED_ID.ordinal()), MessageBuilder.buildNotInterested());
    }

    @Test
    public void testBuildHave() throws Exception {
        byte[] haveId = intToByte(Message.MessageID.HAVE_ID.ordinal());
        byte[] pieceIndex = intToByte(12);
        byte[] message = new byte[haveId.length + pieceIndex.length];
        System.arraycopy(haveId, 0, message, 0, haveId.length);
        System.arraycopy(pieceIndex, 0, message, haveId.length, pieceIndex.length);
        assertArrayEquals(message, MessageBuilder.buildHave(12));
    }

    @Test
    public void testBuildRequest() throws Exception {
        byte[] requestId = intToByte(Message.MessageID.REQUEST_ID.ordinal());
        byte[] pieceIndex = intToByte(12);
        byte[] begin = intToByte(5);
        byte[] length = intToByte(256);
        byte[] message = new byte[requestId.length + pieceIndex.length + begin.length + length.length];
        System.arraycopy(requestId, 0, message, 0, requestId.length);
        System.arraycopy(pieceIndex, 0, message, 4, pieceIndex.length);
        System.arraycopy(begin, 0, message, 8, begin.length);
        System.arraycopy(length, 0, message, 12, length.length);
        assertArrayEquals(message, MessageBuilder.buildRequest(12, 5, 256));
    }

    @Test
    public void testBuildPiece() throws Exception {
        byte[] pieceId = intToByte(Message.MessageID.PIECE_ID.ordinal());
        byte[] pieceIndex = intToByte(12);
        byte[] begin = intToByte(5);
        byte[] block = "Hello world!".getBytes(StandardCharsets.UTF_8);

        byte[] blockLength = intToByte(block.length);

        byte[] message = new byte[pieceId.length + pieceIndex.length + begin.length + blockLength.length + block.length];

        System.arraycopy(pieceId, 0, message, 0, pieceId.length);
        System.arraycopy(pieceIndex, 0, message, 4, pieceIndex.length);
        System.arraycopy(begin, 0, message, 8, begin.length);
        System.arraycopy(blockLength, 0, message, 12, blockLength.length);
        System.arraycopy(block, 0, message, 16, block.length);
        assertArrayEquals(message, MessageBuilder.buildPiece(12, 5, block));
    }

    @Test
    public void testBuildBitfield() throws Exception {
        byte[] bitfieldId = intToByte(Message.MessageID.BITFIELD_ID.ordinal());
        byte[] bitfield = {0, 0, 1, 0, 1, 1};
        byte[] bitfieldLength = intToByte(bitfield.length);

        byte[] message = new byte[bitfieldId.length + bitfieldLength.length + bitfield.length];

        System.arraycopy(bitfieldId, 0, message, 0, bitfieldId.length);
        System.arraycopy(bitfieldLength, 0, message, 4, bitfieldLength.length);
        System.arraycopy(bitfield, 0, message, 8, bitfield.length);
        assertArrayEquals(message, MessageBuilder.buildBitfield(bitfield));
    }
}
