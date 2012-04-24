import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Server extends JFrame implements ActionListener {

	private static final String USEAGE = "java Server [RTSP listening port]";

	// RTP variables
	private DatagramSocket rtpSocket; 	// socket to be used to send and receive UDP packets
	private DatagramPacket senddp;		// UDP packet containing the video frames
	private InetAddress clientIp;		// Client IP address
	private int rtpDestPort = 0;		// destination port for RTP packets (given by the RTSP Client)

	// GUI
	private JLabel label;

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

	private static int state;					// RTSP Server state == INIT or READY or PLAY
	private Socket rtspSocket;					// socket used to send/receive RTSP messages
	private static BufferedReader rtspReader;	// input stream filters
	private static BufferedWriter rtspWriter;	// output stream filters
	private static String videoName;			// video file requested from the client
	private static int rtspId = 123456;			// ID of the RTSP session
	private int rtspSeqNb = 0;					// Sequence number of RTSP messages within the session

	private final static String CRLF = "\r\n";


	public Server() {

		super("Server");

		timer = new Timer(FRAME_PERIOD, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		// allocate memory for the sending buffer
		buf = new byte[15000];

		// Handler to close the main window
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// stop the timer and exit
				timer.stop();
				System.exit(0);
			}
		});

		// GUI
		label = new JLabel("Send frame #        ", JLabel.CENTER);
		getContentPane().add(label, BorderLayout.CENTER);
	}

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
				label.setText("Send frame #" + imagenb);
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


	public static void main(String argv[]) throws Exception {

		if (argv.length != 1) {
			System.err.printf("Server RTSP listening port required\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		// get RTSP socket port from the command line
		int rtspPort = -1;
		try {
			rtspPort = Integer.parseInt(argv[0]);
		} catch(NumberFormatException e) {
			System.err.printf("Server RTSP listening port must be an integer\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		Server server = new Server();
		server.pack();
		server.setVisible(true);

		// Initiate TCP connection with the client for the RTSP session
		ServerSocket listenSocket = new ServerSocket(rtspPort);
		server.rtspSocket = listenSocket.accept();
		listenSocket.close();

		// Get Client IP address
		server.clientIp = server.rtspSocket.getInetAddress();

		// Initiate RTSPstate
		state = INIT;

		// Set input and output stream filters:
		rtspReader = new BufferedReader(new InputStreamReader(
				server.rtspSocket.getInputStream()));
		rtspWriter = new BufferedWriter(new OutputStreamWriter(
				server.rtspSocket.getOutputStream()));

		// Wait for the SETUP message from the client
		int requestType;
		boolean done = false;
		while (!done) {
			requestType = server.parseRTSPrequest(); // blocking

			if (requestType == SETUP) {
				done = true;

				// update RTSP state
				state = READY;
				System.out.println("New RTSP state: READY");

				// Send response
				server.sendRTSPresponse();

				// init the VideoStream object:
				server.video = new VideoStream(videoName);

				// init RTP socket
				server.rtpSocket = new DatagramSocket();
			}
		}

		// loop to handle RTSP requests
		while (true) {
			// parse the request
			requestType = server.parseRTSPrequest(); // blocking

			if ((requestType == PLAY) && (state == READY)) {
				// send back response
				server.sendRTSPresponse();
				// start timer
				server.timer.start();
				// update state
				state = PLAYING;
				System.out.println("New RTSP state: PLAYING");
			} else if ((requestType == PAUSE) && (state == PLAYING)) {
				// send back response
				server.sendRTSPresponse();
				// stop timer
				server.timer.stop();
				// update state
				state = READY;
				System.out.println("New RTSP state: READY");
			} else if (requestType == TEARDOWN) {
				// send back response
				server.sendRTSPresponse();
				// stop timer
				server.timer.stop();
				// close sockets
				server.rtspSocket.close();
				server.rtpSocket.close();

				System.exit(0);
			}
		}
	}

}