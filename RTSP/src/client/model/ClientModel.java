package client.model;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import shared.RTPpacket;

public class ClientModel extends Observable {

	public enum Update {
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
	private RTPTransport rtpTransport;
	private RTSPTransport rtspTransport;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final Runnable play = new Runnable() {
		public void run() {
			receivePacket();
		}
	};
	private ScheduledFuture<?> playHandle;

	public ClientModel(String videoName, InetAddress serverIp, int serverPort) throws IOException {
		this.videoName = videoName;
		this.sequenceNumber = 0;
		this.sessionId = 0;
		this.rtpTransport = new RTPTransport();
		this.rtspTransport = new RTSPTransport(this, serverIp, serverPort);
		this.rtspTransport.open();
		this.setState(RTSP_STATE.INIT);
	}

	public int setup() throws IOException {
		int responseCode = 0;
		if (getState() == RTSP_STATE.INIT) {
			rtpTransport.open();
			initSequenceNumber();
			rtspTransport.sendRequest("SETUP");
			responseCode = rtspTransport.parseResponse();
			if (responseCode == 200) {
				setState(ClientModel.RTSP_STATE.READY);
			}
		}
		return responseCode;
	}

	public int play() throws IOException {
		int responseCode = 0;
		if (getState() == RTSP_STATE.READY) {
			incrementSequenceNumber();
			rtspTransport.sendRequest("PLAY");
			responseCode = rtspTransport.parseResponse();
			if (responseCode == 200) {
				setState(ClientModel.RTSP_STATE.PLAYING);
				playHandle = scheduler.scheduleAtFixedRate(play, 0, 20, MILLISECONDS);
			}
		}
		return responseCode;
	}

	public int pause() throws IOException {
		int responseCode = 0;
		if (getState() == RTSP_STATE.PLAYING) {
			incrementSequenceNumber();
			rtspTransport.sendRequest("PAUSE");
			responseCode = rtspTransport.parseResponse();
			if (responseCode == 200) {
				setState(RTSP_STATE.READY);
				playHandle.cancel(false);
			}
		}
		return responseCode;
	}

	public int tear() throws IOException {
		int responseCode = 0;
		incrementSequenceNumber();
		rtspTransport.sendRequest("TEARDOWN");
		responseCode = rtspTransport.parseResponse();
		if (responseCode == 200) {
			rtpTransport.close();
			setState(ClientModel.RTSP_STATE.INIT);
			playHandle.cancel(false);
		}
		return responseCode;
	}

	private void receivePacket() {
		try {
			RTPpacket rtpPacket = rtpTransport.receivePacket();
			int payloadLength = rtpPacket.getPayloadLength();
			frame = new byte[payloadLength];
			rtpPacket.getPayload(frame);
			this.setChanged();
			this.notifyObservers(Update.FRAME);
		} catch (InterruptedIOException iioe) {
			// System.out.println("Nothing to read");
		} catch (IOException ioe) {
			System.out.println("I/O exception receiving a RTSP packet: " + ioe.getMessage());
		}
	}

	public RTSP_STATE getState() {
		return state;
	}

	public void setState(RTSP_STATE state) {
		this.state = state;
		System.out.printf("New client RTSP state: %s\n", state);
		this.setChanged();
		this.notifyObservers(Update.STATE);
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void initSequenceNumber() {
		sequenceNumber = 1;
		this.setChanged();
		this.notifyObservers(Update.SEQUENCE);
	}

	public void incrementSequenceNumber() {
		sequenceNumber++;
		this.setChanged();
		this.notifyObservers(Update.SEQUENCE);
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
		this.notifyObservers(Update.SESSION);
	}

	public String getVideoName() {
		return videoName;
	}

	public int getSessionId() {
		return sessionId;
	}

}