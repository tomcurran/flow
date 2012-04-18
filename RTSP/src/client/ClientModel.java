package client;

import java.util.Observable;

public class ClientModel extends Observable {

	public static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video

	public enum UpdateReason {
		STATE,
		SEQUENCE,
		SESSION,
		FRAME
	}

	public enum RTSP_STATE {
		NONE,
		INIT,
		READY,
		PLAYING
	}

	private RTSP_STATE state;		// RTSP state == INIT or READY or PLAYING
	private int sequenceNumber;		// Sequence number of RTSP messages within the session
	private int sessionId;			// ID of the RTSP session (given by the RTSP Server)
	private String videoName;		// video file name
	private byte[] frame;			// latest frame to be received

	public ClientModel(String videoName) {
		this.videoName = videoName;
		this.sequenceNumber = 0;
		this.sessionId = 0;
		this.state = RTSP_STATE.NONE;
	}

	public RTSP_STATE getState() {
		return state;
	}

	public void setState(RTSP_STATE state) {
		this.state = state;
        this.setChanged();
        this.notifyObservers(UpdateReason.STATE);
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int i) {
		sequenceNumber = i;
        this.setChanged();
        this.notifyObservers(UpdateReason.SEQUENCE);
	}

	public void setFrame(byte[] payload, int payloadLength) {
		frame = new byte[payloadLength];
		frame = payload;
        this.setChanged();
        this.notifyObservers(UpdateReason.FRAME);
	}

	public byte[] getFrame() {
		return frame;
	}

	public int getFrameLength() {
		return frame != null ? frame.length : 0;
	}

	public void setSessionId(int id) {
		sessionId = id;
        this.setChanged();
        this.notifyObservers(UpdateReason.SESSION);
	}

	public String getVideoName() {
		return videoName;
	}

	public int getSessionId() {
		return sessionId;
	}

}