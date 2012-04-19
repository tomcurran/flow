package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.util.Observable;

import javax.swing.Timer;

import shared.RTPpacket;

public class ClientModel extends Observable {

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
	private RTPTransport rtpTransport;
	private RTSPTransport rtspTransport;
	private Timer timer; // timer used to receive data from the UDP socket

	public ClientModel(String videoName, InetAddress serverIp, int serverPort) throws IOException {
		this.videoName = videoName;
		this.sequenceNumber = 0;
		this.sessionId = 0;
		this.rtpTransport = new RTPTransport();
		this.rtspTransport = new RTSPTransport(this, serverIp, serverPort);
		this.rtspTransport.open();
		this.setState(RTSP_STATE.INIT);
		timer = new Timer(20, new TimerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
	}

	public RTSP_STATE getState() {
		return state;
	}

	public void setState(RTSP_STATE state) {
		this.state = state;
		System.out.printf("New client RTSP state: %s\n", state);
		this.setChanged();
		this.notifyObservers(UpdateReason.STATE);
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void initSequenceNumber() {
		sequenceNumber = 1;
		this.setChanged();
		this.notifyObservers(UpdateReason.SEQUENCE);
	}

	public void incrementSequenceNumber() {
		sequenceNumber++;
		this.setChanged();
		this.notifyObservers(UpdateReason.SEQUENCE);
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

	public int setup() throws IOException {
		int responseCode = 0;
		if (getState() == RTSP_STATE.INIT) {
			// Init non-blocking RTPsocket that will be used to receive data
			rtpTransport.open();

			// init RTSP sequence number
			initSequenceNumber();

			// Send SETUP message to the server
			rtspTransport.sendRequest("SETUP");

			// Wait for the response
			responseCode = rtspTransport.parseResponse();
			if (responseCode == 200) {
				// change RTSP state and print new state
				setState(ClientModel.RTSP_STATE.READY);
			}
		}
		return responseCode;
	}

	public int play() throws IOException {
		int responseCode = 0;
		if (getState() == RTSP_STATE.READY) {
			// increase RTSP sequence number
			incrementSequenceNumber();

			// Send PLAY message to the server
			rtspTransport.sendRequest("PLAY");

			// Wait for the response
			responseCode = rtspTransport.parseResponse();
			if (responseCode == 200) {
				// change RTSP state and print out new state
				setState(ClientModel.RTSP_STATE.PLAYING);
				timer.start();
			}
		}
		return responseCode;
	}

	public int pause() throws IOException {
		int responseCode = 0;
		if (getState() == RTSP_STATE.PLAYING) {
			// increase RTSP sequence number
			incrementSequenceNumber();

			// Send PAUSE message to the server
			rtspTransport.sendRequest("PAUSE");

			// Wait for the response
			responseCode = rtspTransport.parseResponse();
			if (responseCode == 200) {
				// change RTSP state and print out new state
				setState(RTSP_STATE.READY);
				timer.stop();
			}
		}
		return responseCode;
	}

	public int tear() throws IOException {
		int responseCode = 0;
		incrementSequenceNumber();

		// Send TEARDOWN message to the server
		rtspTransport.sendRequest("TEARDOWN");

		// Wait for the response
		responseCode = rtspTransport.parseResponse();
		if (responseCode == 200) {
			// change RTSP state and print out new state
			rtpTransport.close();
			setState(ClientModel.RTSP_STATE.INIT);
			timer.stop();
		}
		return responseCode;
	}

	public void receivePacket() throws IOException {
		RTPpacket rtpPacket = rtpTransport.receivePacket();

		// get the payload bitstream from the RTPpacket object
		int payloadLength = rtpPacket.getPayloadLength();
		frame = new byte[payloadLength];
		rtpPacket.getPayload(frame);

		this.setChanged();
		this.notifyObservers(UpdateReason.FRAME);
	}

	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				receivePacket();
			} catch (InterruptedIOException iioe) {
//				System.out.println("Nothing to read");
			} catch (IOException ioe) {
				System.out.println("I/O exception receiving a RTSP packet: " + ioe.getMessage());
			}
		}
	}

}