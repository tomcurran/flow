package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.Timer;

import shared.RTPpacket;
import client.ClientModel.RTSP_STATE;

public class ClientController {

	private ClientModel model;
	private RTSPTransport rtspTransport;
	private RTPTransport rtpTransport;
	private Timer timer; // timer used to receive data from the UDP socket

	public ClientController(ClientModel model, InetAddress serverIp, int serverPort) {
		this.model = model;
		
		rtpTransport = new RTPTransport();
		
		try {
			rtspTransport = new RTSPTransport(model, serverIp, serverPort);

			// init RTSP state
			model.setState(RTSP_STATE.INIT);
		} catch (IOException e) {
			System.err.printf("I/O exception connecting to RTSP server: %s\n", e.getMessage());
			System.exit(0);
		}

		timer = new Timer(20, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
	}

	public void setup() {
		if (model.getState() == RTSP_STATE.INIT) {
			// Init non-blocking RTPsocket that will be used to receive data
			try {
				rtpTransport.setup();

			} catch (SocketException se) {
				System.out.println("Socket exception: " + se);
				System.exit(0);
			}

			// init RTSP sequence number
			model.initSequenceNumber();

			// Send SETUP message to the server
			rtspTransport.sendRequest("SETUP");

			// Wait for the response
			if (rtspTransport.parseResponse() != 200) {
				System.out.println("Invalid Server Response");
			} else {
				// change RTSP state and print new state
				model.setState(ClientModel.RTSP_STATE.READY);
				System.out.println("New RTSP state: Ready");
			}
		}
	}

	public void play() {
		if (model.getState() == RTSP_STATE.READY) {
			// increase RTSP sequence number
			model.incrementSequenceNumber();

			// Send PLAY message to the server
			rtspTransport.sendRequest("PLAY");

			// Wait for the response
			if (rtspTransport.parseResponse() != 200) {
				System.out.println("Invalid Server Response");
			} else {
				// change RTSP state and print out new state
				model.setState(ClientModel.RTSP_STATE.PLAYING);
				System.out.println("New RTSP state: Playing");

				timer.start();
			}
		}
	}

	public void pause() {
		if (model.getState() == RTSP_STATE.PLAYING) {
			// increase RTSP sequence number
			model.incrementSequenceNumber();

			// Send PAUSE message to the server
			rtspTransport.sendRequest("PAUSE");

			// Wait for the response
			if (rtspTransport.parseResponse() != 200) {
				System.out.println("Invalid Server Response");
			} else {
				// change RTSP state and print out new state
				model.setState(RTSP_STATE.READY);
				System.out.println("New RTSP state: Ready");

				timer.stop();
			}
		}
	}

	public void tear() {
		model.incrementSequenceNumber();

		// Send TEARDOWN message to the server
		rtspTransport.sendRequest("TEARDOWN");

		// Wait for the response
		if (rtspTransport.parseResponse() != 200) {
			System.out.println("Invalid Server Response");
		} else {
			// change RTSP state and print out new state
			model.setState(ClientModel.RTSP_STATE.INIT);
			System.out.println("New RTSP state: INIT");

			timer.stop();
			System.exit(0);
		}
	}

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				// create an RTPpacket object from the ...
				RTPpacket rtpPacket = rtpTransport.receivePacket();

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

}