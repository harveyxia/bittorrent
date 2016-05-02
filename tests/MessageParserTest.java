package tests;

import org.junit.Test;
import utils.MessageBuilder;
import utils.MessageParser;

import static org.junit.Assert.*;

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
}
