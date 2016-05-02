package message;

/**
 * Object representation of piece message fields.
 */
public class Piece {

    private int pieceIndex;
    private int begin;
    private byte[] block;

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
