package utils;

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
}
