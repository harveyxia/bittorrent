package tests;

import metafile.Info;
import metafile.MetaFile;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;

/**
 * Test metafile parsing.
 */
public class MetaFileUtilsTest {

    @Test
    public void testParseMetafile() throws Exception {
        MetaFile metaFile = MetaFile.parseMetafile("tests/test.torrent");
        Info info = metaFile.getInfo();
        assertEquals(new InetSocketAddress("localhost", 6789), metaFile.getAnnounce());
        assertEquals("testData.txt", info.getFilename());
        assertEquals(256, info.getPieceLength());
        assertEquals(10988, info.getFileLength());
    }
}
