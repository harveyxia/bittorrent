package metafile;

/**
 * Object representation of metafile (.torrent).
 */
public class Metafile {
    private Info info;
    private String announce;

    public Metafile(Info info, String announce) {
        this.info = info;
        this.announce = announce;
    }

    public Info getInfo() {
        return info;
    }

    public String getAnnounce() {
        return announce;
    }
}
