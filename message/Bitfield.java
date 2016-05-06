package message;

/**
 * Representation of bitfield.
 */
public class Bitfield {

    private byte[] bitfield;

    public Bitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    public byte[] getByteArray() {
        return bitfield;
    }

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

    public synchronized void setPieceToCompleted(int pieceIndex) {
        bitfield[pieceIndex] = 1;
    }

    public synchronized void setPieceToRequested(int pieceIndex) {
        bitfield[pieceIndex] = 2;
    }


    /**
     * Return true if all bitfield slots are 1.
     */
    public boolean isCompleted() {
        for (int i = 0; i < bitfield.length; i++) {
            if (bitfield[i] == 0) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        String bitfieldString = "";
        for (int i = 0; i < bitfield.length; i++) {
            bitfieldString = bitfieldString.concat(Integer.toString(bitfield[i]));
        }
        return bitfieldString;
    }

}
