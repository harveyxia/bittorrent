package message;

/**
 * Representation of request message fields.
 */
public class Request {

    private int pieceIndex;
    private int begin;
    private int length;

    public Request(int pieceIndex, int begin, int length) {
        this.pieceIndex = pieceIndex;
        this.begin = begin;
        this.length = length;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public int getBegin() {
        return begin;
    }

    public int getLength() {
        return length;
    }
}
