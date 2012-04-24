package server.rtsp.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;

import javax.swing.Timer;

public class RTSPRequest extends Observable implements ActionListener, Runnable {

	private static int MJPEG_TYPE = 26;		// RTP payload type for MJPEG video
	private static int FRAME_PERIOD = 100;	// Frame period of the video to stream, in ms
	private static int VIDEO_LENGTH = 500;	// length of the video in frames

	private static int rtspId = 0;			// ID of the RTSP session

	public enum Update {
		STATE,
		SEQUENCE,
		FRAME
	}

	public enum RTSP_STATES {
		INIT,
		READY,
		PLAYING
	}

	public enum RTSP_METHODS {
		NONE,
		SETUP,
		PLAY,
		PAUSE,
		TEARDOWN
	}

	private RTPTransport rtpTransport;
	private RTSPTransport rtspTransport;
	private RTSP_STATES state;
	private int sequenceNumber;				// Sequence number of RTSP messages within the session
	private String videoName;				// video file requested from the client
	private VideoStream video;				// VideoStream object used to access video frames
	private int frameNumber;				// image nb of the image currently transmitted
	private Timer timer;					// timer used to send the images at the video frame rate
	private byte[] buffer;					// buffer used to store the images to send to the client

	public RTSPRequest (Socket socket) {
		rtspTransport = new RTSPTransport(this, socket);
		rtpTransport = new RTPTransport();
		sequenceNumber = 0;
		frameNumber = 0;
		rtspId++;

		timer = new Timer(FRAME_PERIOD, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		// allocate memory for the sending buffer
		buffer = new byte[15000];
	}

	@Override
	public void run() {
		doServerWait();
	}

	private void doServerWait() {
		try {
	
			// Get Client IP address
			rtpTransport.setClientIp(rtspTransport.getClientIp());
		
			// Initiate RTSPstate
			setState(RTSP_STATES.READY);
		
			// Set input and output stream filters:
			rtspTransport.open();
			
		} catch (IOException ioex) {
			System.err.println("Error on socket (Port: "+rtspTransport.getClientPort()+") at ServerModel initialisation");
			System.err.println(ioex.getMessage());
			System.exit(1);
		}
		
		// Wait for the SETUP message from the client
		RTSP_METHODS request;
		boolean done = false;
		while (!done) {
			request = rtspTransport.parseRequest(); // blocking

			if (request == RTSP_METHODS.SETUP) {
				done = true;

				// update RTSP state
				setState(RTSP_STATES.READY);

				// Send response
				rtspTransport.sendResponse();

				// init the VideoStream object:
				try {
					video = new VideoStream(videoName);
				} catch (Exception e1) {
					System.err.println("Problem creating video stream");
					System.err.println(e1.getMessage());
					e1.printStackTrace();
				}

				// init RTP socket
				try {
					RTPTransport.open();
				} catch (SocketException e) {
					System.err.println("Problem creating RTP socket");
					System.exit(1);
				}
			}
		}

		// loop to handle RTSP requests
		while (true) {
			// parse the request
			request = rtspTransport.parseRequest(); // blocking

			if ((request == RTSP_METHODS.PLAY) && (getState() == RTSP_STATES.READY)) {
				// send back response
				rtspTransport.sendResponse();
				// start timer
				timer.start();
				// update state
				setState(RTSP_STATES.PLAYING);
			} else if ((request == RTSP_METHODS.PAUSE) && (getState() == RTSP_STATES.PLAYING)) {
				// send back response
				rtspTransport.sendResponse();
				// stop timer
				timer.stop();
				// update state
				setState(RTSP_STATES.READY);
			} else if (request == RTSP_METHODS.TEARDOWN) {
				// send back response
				rtspTransport.sendResponse();
				// stop timer
				timer.stop();
				// close sockets
				try {
					rtspTransport.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Problem closing RTSP socket (Port: "+rtspTransport.getClientPort()+")");
					System.exit(1);
				}
				RTPTransport.close();

				System.exit(0);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// if the current image nb is less than the length of the video
		if (frameNumber < VIDEO_LENGTH) {
			// update current imagenb
			frameNumber++;

			try {
				// get next frame to send from the video, as well as its size
				int imageLength = video.getnextframe(buffer);

				// Builds an RTPpacket object containing the frame
				RTPpacket rtpPacket = new RTPpacket(MJPEG_TYPE, frameNumber,
						frameNumber * FRAME_PERIOD, buffer, imageLength);

				// get to total length of the full rtp packet to send
				int packetLength = rtpPacket.getLength();

				// retrieve the packet bitstream and store it in an array of bytes
				byte[] packetBits = new byte[packetLength];
				rtpPacket.getPacket(packetBits);

//				System.out.println("Send frame #" + imagenb);
				rtpTransport.send(packetBits, packetLength);

				// update GUI
				this.setChanged();
				this.notifyObservers(Update.FRAME);
			} catch (Exception ex) {
				System.out.println("Exception caught: " + ex);
				System.exit(0);
			}
		} else {
			// if we have reached the end of the video file, stop the timer
			timer.stop();
		}
	}

	// TODO - this will likely be a kill-thread or safe pre-shutdown command
	public void stopTimer() {
		timer.stop();
	}

	public int getSquenceNumber() {
		return sequenceNumber;
	}

	public int getSessionId() {
		return rtspId;
	}

	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
		this.setChanged();
		this.notifyObservers(Update.FRAME);
	}

	public int getFrameNumber() {
		return frameNumber;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		this.setChanged();
		this.notifyObservers(Update.SEQUENCE);
	}

	public void setClientPort(int port) {
		rtpTransport.setClientPort(port);
	}

	public void setState(RTSP_STATES state) {
		this.state = state;
		System.out.println("New RTSP state: " + state);
		this.setChanged();
		this.notifyObservers(Update.STATE);
	}

	public RTSP_STATES getState() {
		return state;
	}
	
}
