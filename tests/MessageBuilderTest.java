package tests;

import org.junit.Test;
import utils.MessageBuilder;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * Harvey Xia.
 */
public class MessageBuilderTest {

    @Test
    public void testBuildChoke() throws Exception {
        assertEquals(MessageBuilder.buildChoke(), MessageBuilder.buildChoke());
    }

    @Test
    public void testBuildInterested() throws Exception {
        assertEquals("interested:", MessageBuilder.buildInterested());
    }

    @Test
    public void testBuildNotInterested() throws Exception {
        assertEquals("not interested:", MessageBuilder.buildNotInterested());
    }

    @Test
    public void testBuildHave() throws Exception {
        assertEquals("have:12", MessageBuilder.buildHave(12));
    }

    @Test
    public void testBuildRequest() throws Exception {
        assertEquals("request:1,0,256", MessageBuilder.buildRequest(1, 0, 256));
    }

    @Test
    public void testBuildPiece() throws Exception {
        String data = "Hello world!";
        assertEquals("piece:1,0," + data, MessageBuilder.buildPiece(1, 0, data.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testBuildBitfield() throws Exception {
        byte[] bitfield = {0, 0, 1, 0, 1, 1};
        assertEquals("bitfield:" + new String(bitfield, StandardCharsets.UTF_8), MessageBuilder.buildBitfield(bitfield));
    }
}
