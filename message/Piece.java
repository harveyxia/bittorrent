package message;

/**
 * Object representation of piece message fields.
 */
public class Piece {

    private int pieceIndex;  // integer specifying the zero-based piece index
    private int begin;       // integer specifying the zero-based byte offset within the piece
    private byte[] block;    // block of data

    public Piece(int pieceIndex, int begin, byte[] block) {
        this.pieceIndex = pieceIndex;
        this.begin = begin;
        this.block = block;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public int getBegin() {
        return begin;
    }

    public byte[] getBlock() {
        return block;
    }
}
