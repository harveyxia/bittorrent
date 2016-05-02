package tests;

import metafile.Info;
import metafile.Metafile;
import metafile.MetafileUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test metafile parsing.
 */
public class MetafileUtilsTest {

    @Test
    public void testParseMetafile() throws Exception {
        Metafile metafile = MetafileUtils.parseMetafile("test.torrent");
        Info info = metafile.getInfo();
        assertEquals("localhost:6789", metafile.getAnnounce());
        assertEquals("testData.txt", info.getFilename());
        assertEquals(256, info.getPieceLength());
        assertEquals(1000, info.getFileLength());
    }
}
