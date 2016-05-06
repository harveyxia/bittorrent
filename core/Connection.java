package core;

import message.Bitfield;

import java.net.Socket;

/**
 * Representation of client to client state.
 */
public class Connection {

    private boolean established;

    private State downloadState;
    private State uploadState;

    private Bitfield bitfield;            // bitfield of the peer
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

    public Bitfield getBitfield() {
        return bitfield;
    }

    public void setBitfield(Bitfield bitfield) {
        this.bitfield = bitfield;
    }
}
