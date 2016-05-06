package core;

import java.net.Socket;

/**
 * Representation of client to client state.
 */
public class Connection {

    private boolean established;

    private State downloadState;
    private State uploadState;

    private byte[] bitfield;            // bitfield of the peer
    private Socket socket;

    public Connection(Socket socket, boolean established, State downloadState, State uploadState) {
        this.socket = socket;
        this.established = established;
        this.downloadState = downloadState;
        this.uploadState = uploadState;
    }

    public static Connection getInitialState(Socket socket) {
        State download = State.getInitialState();
        State upload = State.getInitialState();
        return new Connection(socket, true, download, upload);
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
        return downloadState.isInterested() && !downloadState.isChoked();
    }

    public boolean canUploadTo() {
        return !uploadState.isChoked() && uploadState.isInterested();
    }

    public State getDownloadState() {
        return downloadState;
    }

    public State getUploadState() {
        return uploadState;
    }
}
