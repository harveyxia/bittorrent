package message;

/**
 * Representation of bitfield.
 */
public class Bitfield {

    private byte[] bitfield;

    public Bitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    public byte[] getBitfield() {
        return bitfield;
    }

    public String toString() {
        String bitfieldString = "";
        for (int i = 0; i < bitfield.length; i++) {
            bitfieldString = bitfieldString.concat(Integer.toString(bitfield[i]));
        }
        return bitfieldString;
    }
}
