package metafile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Parse metafiles.
 */
public class MetafileUtils {

    public static final String INFO = "info";
    public static final String FILENAME = "filename";
    public static final String PIECE_LENGTH = "pieceLength";
    public static final String FILE_LENGTH = "fileLength";
    public static final String ANNOUNCE = "announce";

    /**
     * Parse .torrent file.
     *
     * @param filename The metafile.
     * @return metafile.
     */
    public static Metafile parseMetafile(String filename) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filename)), "UTF-8");
            JSONObject metafileJson = new JSONObject(json);
            JSONObject infoJson = metafileJson.getJSONObject(INFO);
            Info info = parseInfoJson(infoJson);
            String announce = metafileJson.getString(ANNOUNCE);
            return new Metafile(info, announce);
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
}
