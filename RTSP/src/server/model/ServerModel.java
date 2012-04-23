package server.model;

import shared.RTPpacket;
import shared.VideoStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.StringTokenizer;

import javax.swing.Timer;


	/**
	 * @author jwb09119
	 * @date 18/04/2012
	 * 
	 * This class will hold the main functional thread of the Server and allocate
	 * incoming requests to various thread pools
	 *
	 */

public class ServerModel extends Observable implements ActionListener {

	private static final String USEAGE = "java Server [RTSP listening port]";

	// RTP variables
	private DatagramSocket rtpSocket; 	// socket to be used to send and receive UDP packets
	private DatagramPacket senddp;		// UDP packet containing the video frames
	private InetAddress clientIp;		// Client IP address
	private int rtpDestPort = 0;		// destination port for RTP packets (given by the RTSP Client)

	// Video variables
	private int imagenb = 0;				// image nb of the image currently transmitted
	private VideoStream video;				// VideoStream object used to access video frames
	private static int MJPEG_TYPE = 26;		// RTP payload type for MJPEG video
	private static int FRAME_PERIOD = 100;	// Frame period of the video to stream, in ms
	private static int VIDEO_LENGTH = 500;	// length of the video in frames

	private Timer timer;	// timer used to send the images at the video frame rate
	private byte[] buf;		// buffer used to store the images to send to the client

	// RTSP variables
	// rtsp states
	private final static int INIT = 0;
	private final static int READY = 1;
	private final static int PLAYING = 2;
	// rtsp message types
	private final static int SETUP = 3;
	private final static int PLAY = 4;
	private final static int PAUSE = 5;
	private final static int TEARDOWN = 6;

	private static int rtspPort;
	private static int state;					// RTSP Server state == INIT or READY or PLAY
	private Socket rtspSocket;					// socket used to send/receive RTSP messages
	private static BufferedReader rtspReader;	// input stream filters
	private static BufferedWriter rtspWriter;	// output stream filters
	private static String videoName;			// video file requested from the client
	private static int rtspId = 123456;			// ID of the RTSP session
	private int rtspSeqNb = 0;					// Sequence number of RTSP messages within the session

	private final static String CRLF = "\r\n";

	// Constructor
	public ServerModel (int rtspPort) {
		
		this.rtspPort = rtspPort;
		timer = new Timer(FRAME_PERIOD, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
		
		// allocate memory for the sending buffer
		buf = new byte[15000];

		// doServerWait();
		
	}
	
	
	public void startServer() {
		doServerWait();
	}
	
	
	private void doServerWait() {
		try {
			// Initiate TCP connection with the client for the RTSP session
			ServerSocket listenSocket = new ServerSocket(rtspPort);
			rtspSocket = listenSocket.accept();
			listenSocket.close();
		
			// Get Client IP address
			this.clientIp = rtspSocket.getInetAddress();
		
			// Initiate RTSPstate
			state = INIT;
			this.setChanged();
			this.notifyObservers();
		
			// Set input and output stream filters:
			rtspReader = new BufferedReader(new InputStreamReader(
					rtspSocket.getInputStream()));
			rtspWriter = new BufferedWriter(new OutputStreamWriter(
					rtspSocket.getOutputStream()));
			
		} catch (IOException ioex) {
			System.err.println("Error on socket (Port: "+rtspPort+") at ServerModel initialisation");
			System.err.println(ioex.getMessage());
			System.exit(1);
		}
		
		// Wait for the SETUP message from the client
		int requestType;
		boolean done = false;
		while (!done) {
			requestType = parseRTSPrequest(); // blocking

			if (requestType == SETUP) {
				done = true;

				// update RTSP state
				state = READY;
				this.setChanged();
				this.notifyObservers();
				System.out.println("New RTSP state: READY");

				// Send response
				sendRTSPresponse();

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
					rtpSocket = new DatagramSocket();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Problem creating RTP socket");
					System.exit(1);
				}
			}
		}

		// loop to handle RTSP requests
		while (true) {
			// parse the request
			requestType = parseRTSPrequest(); // blocking

			if ((requestType == PLAY) && (state == READY)) {
				// send back response
				sendRTSPresponse();
				// start timer
				timer.start();
				// update state
				state = PLAYING;
				this.setChanged();
				this.notifyObservers();
				System.out.println("New RTSP state: PLAYING");
			} else if ((requestType == PAUSE) && (state == PLAYING)) {
				// send back response
				sendRTSPresponse();
				// stop timer
				timer.stop();
				// update state
				state = READY;
				this.setChanged();
				this.notifyObservers();
				System.out.println("New RTSP state: READY");
			} else if (requestType == TEARDOWN) {
				// send back response
				sendRTSPresponse();
				// stop timer
				timer.stop();
				// close sockets
				try {
					rtspSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Problem closing RTSP socket (Port: "+rtspSocket.getPort()+")");
					System.exit(1);
				}
				rtpSocket.close();

				System.exit(0);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// if the current image nb is less than the length of the video
		if (imagenb < VIDEO_LENGTH) {
			// update current imagenb
			imagenb++;

			try {
				// get next frame to send from the video, as well as its size
				int imageLength = video.getnextframe(buf);

				// Builds an RTPpacket object containing the frame
				RTPpacket rtpPacket = new RTPpacket(MJPEG_TYPE, imagenb,
						imagenb * FRAME_PERIOD, buf, imageLength);

				// get to total length of the full rtp packet to send
				int packetLength = rtpPacket.getLength();

				// retrieve the packet bitstream and store it in an array of bytes
				byte[] packetBits = new byte[packetLength];
				rtpPacket.getPacket(packetBits);

				// send the packet as a DatagramPacket over the UDP socket
				senddp = new DatagramPacket(packetBits, packetLength,
						clientIp, rtpDestPort);
				rtpSocket.send(senddp);

				// System.out.println("Send frame #" + imagenb);
				// print the header bitstream
//				rtpPacket.printheader();

				// update GUI
				this.setChanged();
				this.notifyObservers("Send frame #" + imagenb);
			} catch (Exception ex) {
				System.out.println("Exception caught: " + ex);
				System.exit(0);
			}
		} else {
			// if we have reached the end of the video file, stop the timer
			timer.stop();
		}
	}

	
	private int parseRTSPrequest() {
		int requestType = -1;
		try {
			// parse request line and extract the request_type:
			String requestLine = rtspReader.readLine();
			if (requestLine == null) {
				System.out.println("Client ended unexpectedly, forcing teardown.");
				return TEARDOWN;
			}
			System.out.println("RTSP Server - Received from Client:");
			System.out.println(requestLine);

			StringTokenizer tokens = new StringTokenizer(requestLine);
			String requestTypeString = new String(tokens.nextToken());

			// convert to request_type structure:
			if (requestTypeString.compareTo("SETUP") == 0) {
				requestType = SETUP;
			} else if (requestTypeString.compareTo("PLAY") == 0) {
				requestType = PLAY;
			} else if (requestTypeString.compareTo("PAUSE") == 0) {
				requestType = PAUSE;
			} else if (requestTypeString.compareTo("TEARDOWN") == 0) {
				requestType = TEARDOWN;
			}

			if (requestType == SETUP) {
				// extract videoName from requestLine
				videoName = tokens.nextToken();
			}

			// parse the seqNumLine and extract CSeq field
			String seqNumLine = rtspReader.readLine();
			System.out.println(seqNumLine);
			tokens = new StringTokenizer(seqNumLine);
			tokens.nextToken();
			rtspSeqNb = Integer.parseInt(tokens.nextToken());

			// get lastLine
			String lastLine = rtspReader.readLine();
			System.out.println(lastLine);

			if (requestType == SETUP) {
				// extract rtpDestPort from lastLine
				tokens = new StringTokenizer(lastLine);
				for (int i = 0; i < 3; i++) {
					tokens.nextToken(); // skip unused stuff
				}
				rtpDestPort = Integer.parseInt(tokens.nextToken());
			}
			// else lastLine will be the SessionId line ... do not check for now.
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}
		return requestType;
	}

	
	private void sendRTSPresponse() {
		try {
			rtspWriter.write("RTSP/1.0 200 OK" + CRLF);
			rtspWriter.write("CSeq: " + rtspSeqNb + CRLF);
			rtspWriter.write("Session: " + rtspId + CRLF);
			rtspWriter.flush();
			// System.out.println("RTSP Server - Sent response to Client.");
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}
	}
	
	// TODO - this needs to change to be a label once state is
	// handled sensibly. Probably an enum.
	public String getState () {
		return ""+state;
	}
	
	
	// TODO - this will likely be a kill-thread or safe pre-shutdown command
	public void stopTimer() {
		timer.stop();
	}
	
}
