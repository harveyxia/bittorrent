package utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Wrapper around in-progress torrent data files.
 */
public class Datafile {
    //    public static final int PIECE_LENGTH = 512000;
    private final long fileLength;
    private final int pieceLength;
    private final int numPieces;
    private final RandomAccessFile file;
    private final Path dataFolder;
    private String filename;        // treat as unique identifier for file
    private byte[] bitfield;

    // TODO: send pieces in blocks (and maintain internal bitfield with block-granularity?)

    //    public static void main(String[] args) {
    //        try {
    //            RandomAccessFile file = new RandomAccessFile("testfile", "rw");
    //            file.setLength(1000);
    //            file.seek(5);
    //            String s = "hello world!";
    //            file.write(s.getBytes(), 0, s.fileLength());
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //    }

    public Datafile(boolean create, String filename, String directory, long fileLength, int pieceLength) throws IOException {
        this.dataFolder = Paths.get(directory);
        this.filename = filename;
        this.fileLength = fileLength;
        this.pieceLength = pieceLength;
        this.numPieces = (int) Math.ceil(((float) fileLength) / ((float) pieceLength));    // round up
        if (create) {
            file = new RandomAccessFile(dataFolder.toString() + "/" + filename, "rw");
            file.setLength(fileLength);
            bitfield = new byte[numPieces];
        } else {
            file = new RandomAccessFile(dataFolder.toString() + "/" + filename, "r");
            bitfield = new byte[numPieces];
            Arrays.fill(bitfield, (byte) 1);
        }
    }

    public void writePiece(byte[] data, int pieceIndex) {
        long pos = pieceIndex * pieceLength;
        try {
            file.seek(pos);
            file.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        file.close();
    }

    public long getFileLength() {
        return fileLength;
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public int getNumPieces() {
        return numPieces;
    }

    /*** BITFIELD ***/

    /**
     * Return true iff bitfield[pieceIndex] == 1, meaning that piece has been acquired.
     */
    public boolean hasPiece(int pieceIndex) {
        return bitfield[pieceIndex] == 1;
    }

    /**
     * Return true iff bitfield[pieceIndex] == 2, meaning that piece has been requested.
     */
    public boolean requestedPiece(int pieceIndex) {
        return bitfield[pieceIndex] == 2;
    }

    /**
     * Return true iff bitfield[pieceIndex] == 0, meaning that piece is not requested or possessed.
     */
    public boolean missingPiece(int pieceIndex) {
        return bitfield[pieceIndex] == 0;
    }

    public void setPieceToCompleted(int pieceIndex) {
        bitfield[pieceIndex] = 1;
    }

    public void setPieceToRequested(int pieceIndex) {
        bitfield[pieceIndex] = 2;
    }

    public byte[] getBitfield() {
        return bitfield;
    }

    public String getFilename() {
        return filename;
    }
}
