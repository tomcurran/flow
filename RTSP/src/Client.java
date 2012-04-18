
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import shared.RTPpacket;

public class Client {

	private static final String USEAGE = "java Client [Server hostname] [Server RTSP listening port] [Video file requested]";

	// GUI
	private JFrame f = new JFrame("Client");
	private JButton setupButton = new JButton("Setup");
	private JButton playButton = new JButton("Play");
	private JButton pauseButton = new JButton("Pause");
	private JButton tearButton = new JButton("Teardown");
	private JPanel mainPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JLabel iconLabel = new JLabel();
	private ImageIcon icon;

	// RTP variables
	private DatagramPacket rcvdp;				// UDP packet received from the server
	private DatagramSocket rtpSocket;			// socket to be used to send and receive UDP packets
	private static int RTP_RCV_PORT = 25000; 	// port where the client will receive the RTP packets

	private Timer timer; 	// timer used to receive data from the UDP socket
	private byte[] buf; 	// buffer used to store data received from the server

	// RTSP variables
	private final static int INIT = 0;
	private final static int READY = 1;
	private final static int PLAYING = 2;
	private static int state; 					// RTSP state == INIT or READY or PLAYING
	private Socket rtspSocket; 					// socket used to send/receive RTSP messages
	private static BufferedReader rtspReader;	// input stream filter
	private static BufferedWriter rtspWriter;	// output stream filter
	private static String videoName;			// video file to request to the server
	private int rtspSeqNb = 0;					// Sequence number of RTSP messages within the session
	private int trspid = 0;						// ID of the RTSP session (given by the RTSP Server)

	private final static String CRLF = "\r\n";

	// Video constants:
	static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video


	public Client() {

		// Frame
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Buttons
		buttonPanel.setLayout(new GridLayout(1, 0));
		buttonPanel.add(setupButton);
		buttonPanel.add(playButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(tearButton);
		setupButton.addActionListener(new setupButtonListener());
		playButton.addActionListener(new playButtonListener());
		pauseButton.addActionListener(new pauseButtonListener());
		tearButton.addActionListener(new tearButtonListener());

		// Image display label
		iconLabel.setIcon(null);

		// frame layout
		mainPanel.setLayout(null);
		mainPanel.add(iconLabel);
		mainPanel.add(buttonPanel);
		iconLabel.setBounds(0, 0, 380, 280);
		buttonPanel.setBounds(0, 280, 380, 50);

		f.getContentPane().add(mainPanel, BorderLayout.CENTER);
		f.setSize(new Dimension(390, 370));
		f.setVisible(true);

		// init timer
		timer = new Timer(20, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		// allocate enough memory for the buffer used to receive data from the server
		buf = new byte[15000];
	}


	class setupButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			System.out.println("Setup Button pressed !");

			if (state == INIT) {
				// Init non-blocking RTPsocket that will be used to receive data
				try {
					// construct a new DatagramSocket to receive RTP packets
					// from the server, on port RTP_RCV_PORT
					rtpSocket = new DatagramSocket(RTP_RCV_PORT);

					// set TimeOut value of the socket to 5msec.
					rtpSocket.setSoTimeout(5);

				} catch (SocketException se) {
					System.out.println("Socket exception: " + se);
					System.exit(0);
				}

				// init RTSP sequence number
				rtspSeqNb = 1;

				// Send SETUP message to the server
				sendRTSPrequest("SETUP");

				// Wait for the response
				if (parseServerResponse() != 200)
					System.out.println("Invalid Server Response");
				else {
					// change RTSP state and print new state
					state = READY;
					System.out.println("New RTSP state: Ready");
				}
			}// else if state != INIT then do nothing
		}
	}

	class playButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			System.out.println("Play Button pressed !");

			if (state == READY) {
				// increase RTSP sequence number
				rtspSeqNb++;

				// Send PLAY message to the server
				sendRTSPrequest("PLAY");

				// Wait for the response
				if (parseServerResponse() != 200) {
					System.out.println("Invalid Server Response");
				} else {
					// change RTSP state and print out new state
					state = PLAYING;
					System.out.println("New RTSP state: Playing");

					// start the timer
					timer.start();
				}
			}// else if state != READY then do nothing
		}
	}

	class pauseButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			System.out.println("Pause Button pressed !");

			if (state == PLAYING) {
				// increase RTSP sequence number
				rtspSeqNb++;

				// Send PAUSE message to the server
				sendRTSPrequest("PAUSE");

				// Wait for the response
				if (parseServerResponse() != 200) {
					System.out.println("Invalid Server Response");
				} else {
					// change RTSP state and print out new state
					state = READY;
					System.out.println("New RTSP state: Ready");

					// stop the timer
					timer.stop();
				}
			}
			// else if state != PLAYING then do nothing
		}
	}

	class tearButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			System.out.println("Teardown Button pressed !");

			// increase RTSP sequence number
			rtspSeqNb++;

			// Send TEARDOWN message to the server
			sendRTSPrequest("TEARDOWN");

			// Wait for the response
			if (parseServerResponse() != 200)
				System.out.println("Invalid Server Response");
			else {
				// change RTSP state and print out new state
				state = INIT;
				System.out.println("New RTSP state: INIT");

				// stop the timer
				timer.stop();

				// exit
				System.exit(0);
			}
		}
	}

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// Construct a DatagramPacket to receive data from the UDP socket
			rcvdp = new DatagramPacket(buf, buf.length);

			try {
				// receive the DP from the socket:
				rtpSocket.receive(rcvdp);

				// create an RTPpacket object from the DP
				RTPpacket rtpPacket = new RTPpacket(rcvdp.getData(),
						rcvdp.getLength());

				// print important header fields of the RTP packet received:
				System.out.println("Got RTP packet with SeqNum # "+rtpPacket.getSequenceNumber()+" TimeStamp "+rtpPacket.getTimeStamp()+" ms, of type "+rtpPacket.getPayloadType());

				// print header bitstream:
				rtpPacket.printheader();

				// get the payload bitstream from the RTPpacket object
				int payloadLength = rtpPacket.getPayloadLength();
				byte[] payload = new byte[payloadLength];
				rtpPacket.getPayload(payload);

				// get an Image object from the payload bitstream
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Image image = toolkit.createImage(payload, 0, payloadLength);

				// display the image as an ImageIcon object
				icon = new ImageIcon(image);
				iconLabel.setIcon(icon);
			} catch (InterruptedIOException iioe) {
				// System.out.println("Nothing to read");
			} catch (IOException ioe) {
				System.out.println("Exception caught: " + ioe);
			}
		}
	}

	private int parseServerResponse() {
		int replyCode = 0;

		try {
			// parse status line and extract the reply_code:
			String statusLine = rtspReader.readLine();
			System.out.println("RTSP Client - Received from Server:");
			System.out.println(statusLine);

			StringTokenizer tokens = new StringTokenizer(statusLine);
			tokens.nextToken(); // skip over the RTSP version
			replyCode = Integer.parseInt(tokens.nextToken());

			// if reply code is OK get and print the 2 other lines
			if (replyCode == 200) {
				String seqNumLine = rtspReader.readLine();
				System.out.println(seqNumLine);

				String sessionLine = rtspReader.readLine();
				System.out.println(sessionLine);

				// if state == INIT gets the Session Id from the SessionLine
				tokens = new StringTokenizer(sessionLine);
				tokens.nextToken(); // skip over the Session:
				trspid = Integer.parseInt(tokens.nextToken());
			}
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}

		return (replyCode);
	}

	private void sendRTSPrequest(String requestType) {
		try {
			// Use the RTSPBufferedWriter to write to the RTSP socket

			// write the request line:
			rtspWriter.write(requestType + " " + videoName + " "
					+ "RTSP/1.0" + CRLF);

			// write the CSeq line:
			rtspWriter.write("CSeq: " + rtspSeqNb + CRLF);

			// check if request_type is equal to "SETUP" and in this case write
			// the Transport: line advertising to the server the port used to
			// receive the RTP packets RTP_RCV_PORT
			if (requestType.equals("SETUP")) {
				rtspWriter.write("Transport: RTP/UDP; client_port= "
						+ RTP_RCV_PORT + CRLF);
			} else {
				// otherwise, write the Session line from the RTSPid field
				rtspWriter.write("Session: " + trspid + CRLF);
			}

			rtspWriter.flush();
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}
	}


	public static void main(String argv[]) {
	
		if (argv.length != 3) {
			System.err.printf("RTSP server host name, listening port and video file required\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		// get server RTSP IP address from the command line
		String serverHost = argv[0];
		InetAddress serverIp = null;
		try {
			serverIp = InetAddress.getByName(serverHost);
		} catch (UnknownHostException e) {
			System.err.printf("RTSP server host not found: %s\n", serverHost);
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		// get server RTSP port from the command line
		int rtspServerPort = 0;
		try {
			rtspServerPort = Integer.parseInt(argv[1]);
		} catch(NumberFormatException e) {
			System.err.printf("RTSP server port must be an integer\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}
		
		// get video file name  from the command line
		videoName = argv[2];

		Client client = new Client();

		try {
		// Establish a TCP connection with the server to exchange RTSP messages
		client.rtspSocket = new Socket(serverIp, rtspServerPort);

		// Set input and output stream filters:
		rtspReader = new BufferedReader(new InputStreamReader(
				client.rtspSocket.getInputStream()));
		rtspWriter = new BufferedWriter(new OutputStreamWriter(
				client.rtspSocket.getOutputStream()));

		// init RTSP state:
		state = INIT;
		} catch (IOException e) {
			System.err.printf("I/O exception connecting to RTSP server: %s\n", e.getMessage());
			System.exit(0);
		}
	}

}