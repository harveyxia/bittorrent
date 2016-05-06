package core;

import java.net.Socket;

/**
 * Representation of client to client state.
 */
public class ConnectionState {

    private boolean established;
    private boolean amChoking;          // if true, this client is choking the peer
    private boolean amInterested;       // if true, this client is interested in the peer
    private boolean peerChoking;        // if true, the peer is choking this client
    private boolean peerInterested;     // if true, the peer is interested in this client
    private byte[] bitfield;            // bitfield of the peer
    private Socket socket;

    public ConnectionState(Socket socket, boolean amChoking, boolean amInterested, boolean peerChoking, boolean peerInterested, boolean established) {
        this.socket = socket;
        this.amChoking = amChoking;
        this.amInterested = amInterested;
        this.peerChoking = peerChoking;
        this.peerInterested = peerInterested;
        this.established = established;
    }

    public static ConnectionState getInitialState(Socket socket) {
        return new ConnectionState(socket, true, false, true, false, false);
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

    public Socket getSocket() {
        return socket;
    }

    /************
     * BITFIELD
     * **********/

    /**
     * Return true iff bitfield[pieceIndex] == 1, meaning that piece has been acquired.
     */
    public boolean hasPiece(int pieceIndex) {
        return bitfield[pieceIndex] == 1;
    }

    /**
     * Return true iff bitfield[pieceIndex] == 0, meaning that piece is not requested or possessed.
     */
    public boolean missingPiece(int pieceIndex) {
        return bitfield[pieceIndex] == 0;
    }

    public void setPieceToHave(int pieceIndex) {
        bitfield[pieceIndex] = 1;
    }

    public byte[] getBitfield() {
        return bitfield;
    }

    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    /**********************
     * DOWNLOAD
     ***********************/

    public boolean canDownloadFrom() {
        return amInterested && !peerChoking;
    }

    public boolean canUploadTo() {
        return !amChoking && peerInterested;
    }
}
