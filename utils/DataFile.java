package utils;

import message.Bitfield;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Wrapper around in-progress torrent data files.
 */
public class Datafile {
    public static final String MODE = "rws";    // Read and write synchronously for thread-safety
    private final long fileLength;
    private final int pieceLength;
    private final int numPieces;
    private final RandomAccessFile file;
    private final Path dataFolder;
    private final String filename;        // treat as unique identifier for file
    private final Bitfield bitfield;

    public Datafile(boolean create, String filename, String directory, long fileLength, int pieceLength) throws IOException {
        this.dataFolder = Paths.get(directory);
        this.filename = filename;
        this.fileLength = fileLength;
        this.pieceLength = pieceLength;
        this.numPieces = (int) Math.ceil(((float) fileLength) / ((float) pieceLength));    // round up
        if (create) {
            file = new RandomAccessFile(dataFolder.toString() + "/" + filename, MODE);
            System.out.println("set file length to " + fileLength);
            file.setLength(fileLength);
            bitfield = new Bitfield(new byte[numPieces]);
        } else {
            file = new RandomAccessFile(dataFolder.toString() + "/" + filename, "r");
            byte[] bitfieldArray = new byte[numPieces];
            Arrays.fill(bitfieldArray, (byte) 1);
            bitfield = new Bitfield(bitfieldArray);
        }
    }

    public void writePiece(byte[] data, int pieceIndex) {
        synchronized (file) {
            long pos = pieceIndex * pieceLength;
            try {
                file.seek(pos);
                file.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] readPiece(int pieceIndex) {
        synchronized (file) {
            long pos = pieceIndex * pieceLength;
            int length = Math.min(pieceLength, (int) (fileLength - pos));
            byte[] piece = new byte[length];
            try {
                file.seek(pos);
                file.read(piece, 0, length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return piece;
        }
    }

    public void close() throws IOException {
        synchronized (file) {
            file.close();
        }
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

    public Bitfield getBitfield() {
        return bitfield;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isCompleted() {
        return bitfield.isCompleted();
    }
}
