package core;

/**
 * Representation of client to client state.
 */
public class ConnectionState {

    private boolean established;
    private boolean amChoking;
    private boolean amInterested;
    private boolean peerChoking;
    private boolean peerInterested;
    private byte[] bitfield;

    public ConnectionState(boolean amChoking, boolean amInterested, boolean peerChoking, boolean peerInterested, boolean established) {
        this.amChoking = amChoking;
        this.amInterested = amInterested;
        this.peerChoking = peerChoking;
        this.peerInterested = peerInterested;
        this.established = established;
    }

    public static ConnectionState getInitialState() {
        return new ConnectionState(true, false, true, false, false);
    }

    public boolean isAmChoking() {
        return amChoking;
    }

    public void setAmChoking(boolean amChoking) {
        this.amChoking = amChoking;
    }

    public boolean isAmInterested() {
        return amInterested;
    }

    public void setAmInterested(boolean amInterested) {
        this.amInterested = amInterested;
    }

    public boolean isPeerChoking() {
        return peerChoking;
    }

    public void setPeerChoking(boolean peerChoking) {
        this.peerChoking = peerChoking;
    }

    public boolean isPeerInterested() {
        return peerInterested;
    }

    public void setPeerInterested(boolean peerInterested) {
        this.peerInterested = peerInterested;
    }

    public boolean isEstablished() {
        return established;
    }

    public void setEstablished(boolean established) {
        this.established = established;
    }

    public byte[] getBitfield() {
        return bitfield;
    }

    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    /********************** DOWNLOAD ***********************/

    public boolean canDownloadFrom() {
        return amInterested && !peerChoking;
    }

    public boolean canUploadTo() {
        return !amChoking && peerInterested;
    }
}
