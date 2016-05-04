package metafile;

/**
 * Object representing the info dictionary in a metafile.
 * Currently only supports single file mode.
 */
public class Info {

    private String filename;
    private int pieceLength;
    private long fileLength;
    //    private String[] pieces;    // hash values for each piece, in order

    public Info(String filename, int pieceLength, long fileLength) {
        this.filename = filename;
        this.pieceLength = pieceLength;
        this.fileLength = fileLength;
        //        this.pieces = pieces;
    }

    public String getFilename() {
        return filename;
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public long getFileLength() {
        return fileLength;
    }

    //    public String[] getPieces() {
    //        return pieces;
    //    }
}
