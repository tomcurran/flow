package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.StringTokenizer;

import javax.swing.Timer;

import shared.RTPpacket;
import client.ClientModel.RTSP_STATE;

public class ClientController {

	private DatagramPacket rcvdp;				// UDP packet received from the server
	private DatagramSocket rtpSocket;			// socket to be used to send and receive UDP packets
	private static int RTP_RCV_PORT = 25000; 	// port where the client will receive the RTP packets
	private Timer timer; 	// timer used to receive data from the UDP socket
	private byte[] buf; 	// buffer used to store data received from the server
	private Socket rtspSocket; 					// socket used to send/receive RTSP messages
	private static BufferedReader rtspReader;	// input stream filter
	private static BufferedWriter rtspWriter;	// output stream filter
	private final static String CRLF = "\r\n";

	private ClientModel model;

	public ClientController(ClientModel model, InetAddress serverIp, int serverPort) {
		this.model = model;
		
		try {
			// Establish a TCP connection with the server to exchange RTSP messages
			rtspSocket = new Socket(serverIp, serverPort);

			// Set input and output stream filters:
			rtspReader = new BufferedReader(new InputStreamReader(
					rtspSocket.getInputStream()));
			rtspWriter = new BufferedWriter(new OutputStreamWriter(
					rtspSocket.getOutputStream()));

			// init RTSP state:
			model.setState(RTSP_STATE.INIT);
		} catch (IOException e) {
			System.err.printf("I/O exception connecting to RTSP server: %s\n", e.getMessage());
			System.exit(0);
		}

		// init timer
		timer = new Timer(20, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		// allocate enough memory for the buffer used to receive data from the server
		buf = new byte[15000];
	}

	public void setup() {
		if (model.getState() == ClientModel.RTSP_STATE.INIT) {
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
			model.setSequenceNumber(1);

			// Send SETUP message to the server
			sendRTSPrequest("SETUP");

			// Wait for the response
			if (parseServerResponse() != 200) {
				System.out.println("Invalid Server Response");
			} else {
				// change RTSP state and print new state
				model.setState(ClientModel.RTSP_STATE.READY);
				System.out.println("New RTSP state: Ready");
			}
		}
	}

	public void play() {
		if (model.getState() == ClientModel.RTSP_STATE.READY) {
			// increase RTSP sequence number
			model.setSequenceNumber(model.getSequenceNumber() + 1);

			// Send PLAY message to the server
			sendRTSPrequest("PLAY");

			// Wait for the response
			if (parseServerResponse() != 200) {
				System.out.println("Invalid Server Response");
			} else {
				// change RTSP state and print out new state
				model.setState(ClientModel.RTSP_STATE.PLAYING);
				System.out.println("New RTSP state: Playing");

				// start the timer
				timer.start();
			}
		}
	}

	public void pause() {
		if (model.getState() == ClientModel.RTSP_STATE.PLAYING) {
			// increase RTSP sequence number
			model.setSequenceNumber(model.getSequenceNumber() + 1);

			// Send PAUSE message to the server
			sendRTSPrequest("PAUSE");

			// Wait for the response
			if (parseServerResponse() != 200) {
				System.out.println("Invalid Server Response");
			} else {
				// change RTSP state and print out new state
				model.setState(ClientModel.RTSP_STATE.READY);
				System.out.println("New RTSP state: Ready");

				// stop the timer
				timer.stop();
			}
		}
	}

	public void tear() {
		model.setSequenceNumber(model.getSequenceNumber() + 1);

		// Send TEARDOWN message to the server
		sendRTSPrequest("TEARDOWN");

		// Wait for the response
		if (parseServerResponse() != 200)
			System.out.println("Invalid Server Response");
		else {
			// change RTSP state and print out new state
			model.setState(ClientModel.RTSP_STATE.INIT);
			System.out.println("New RTSP state: INIT");

			// stop the timer
			timer.stop();

			// exit
			System.exit(0);
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

				model.setFrame(payload, payloadLength);
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
				model.setSessionId(Integer.parseInt(tokens.nextToken()));
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
			rtspWriter.write(requestType + " " + model.getVideoName() + " "
					+ "RTSP/1.0" + CRLF);

			// write the CSeq line:
			rtspWriter.write("CSeq: " + model.getSequenceNumber() + CRLF);

			// check if request_type is equal to "SETUP" and in this case write
			// the Transport: line advertising to the server the port used to
			// receive the RTP packets RTP_RCV_PORT
			if (requestType.equals("SETUP")) {
				rtspWriter.write("Transport: RTP/UDP; client_port= "
						+ RTP_RCV_PORT + CRLF);
			} else {
				// otherwise, write the Session line from the RTSPid field
				rtspWriter.write("Session: " + model.getSessionId() + CRLF);
			}

			rtspWriter.flush();
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}
	}

}