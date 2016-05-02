package core;

/**
 * Representation of client to client state.
 */
public class ConnectionState {

    private boolean amChoking;
    private boolean amInterested;
    private boolean peerChoking;
    private boolean peerInterested;

    public ConnectionState(boolean amChoking, boolean amInterested, boolean peerChoking, boolean peerInterested) {
        this.amChoking = amChoking;
        this.amInterested = amInterested;
        this.peerChoking = peerChoking;
        this.peerInterested = peerInterested;
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
}
