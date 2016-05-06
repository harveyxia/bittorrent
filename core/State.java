package core;

/**
 * Choked and interested.
 */
public class State {

    private boolean choked;
    private boolean interested;

    public State(boolean choked, boolean interested) {
        this.interested = interested;
        this.choked = choked;
    }

    public boolean isChoked() {
        return choked;
    }

    public void setChoked(boolean choked) {
        this.choked = choked;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    // Download: peer is choking me, I am not interested in peer
    // Upload: I am choking peer, peer is not interested in me
    public static State getInitialState() {
        return new State(true, false);
    }
}
