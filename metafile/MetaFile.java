package metafile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Object representation of metafile (.torrent).
 */
public class MetaFile {

    public static final String INFO = "info";
    public static final String FILENAME = "filename";
    public static final String PIECE_LENGTH = "pieceLength";
    public static final String FILE_LENGTH = "fileLength";
    public static final String ANNOUNCE = "announce";
    private Info info;
    private InetSocketAddress announce;

    private MetaFile(Info info, InetSocketAddress announce) {
        this.info = info;
        this.announce = announce;
    }

    /**
     * Parse .torrent file.
     *
     * @param filename The metafile.
     * @return metafile.
     */
    public static MetaFile parseMetafile(String filename) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filename)), "UTF-8");
            JSONObject metafileJson = new JSONObject(json);
            JSONObject infoJson = metafileJson.getJSONObject(INFO);
            Info info = parseInfoJson(infoJson);
            String announce = metafileJson.getString(ANNOUNCE);
            InetAddress host = InetAddress.getByName(announce.substring(0, announce.indexOf(':')));
            int port = Integer.parseInt(announce.substring(announce.indexOf(':') + 1));
            return new MetaFile(info, new InetSocketAddress(host, port));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Info parseInfoJson(JSONObject infoJson) {
        try {
            String filename = infoJson.getString(FILENAME);
            int pieceLength = infoJson.getInt(PIECE_LENGTH);
            long fileLength = infoJson.getLong(FILE_LENGTH);
            return new Info(filename, pieceLength, fileLength);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeTorrent(File f, File data, String host, String port) throws IOException {

        try (PrintWriter w = new PrintWriter(f)) {
            w.println("{");
            w.println("\t\"info\": {");
            w.println("\t\t\"filename\": \""+data.getName()+"\",");
            w.println("\t\t\"fileLength\": "+data.length()+",");
            w.println("\t\t\"pieceLength\": 256000,");
            w.println("\t\t\"pieces\": []");
            w.println("\t},");
            w.println("\t\"announce\": \""+host+":"+port+"\"");
            w.println("}");
        }
    }

    public Info getInfo() {
        return info;
    }

    public InetSocketAddress getAnnounce() {
        return announce;
    }
}
