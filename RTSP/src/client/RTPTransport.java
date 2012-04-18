package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import shared.RTPpacket;

public class RTPTransport {

	private DatagramPacket rcvdp;			// UDP packet received from the server
	private DatagramSocket rtpSocket;		// socket to be used to send and receive UDP packets
	public static int RTP_RCV_PORT = 25000;	// port where the client will receive the RTP packets
	private byte[] buf;						// buffer used to store data received from the server

	public RTPTransport() {
		buf = new byte[15000];	// allocate enough memory for the buffer used to receive data from the server
	}

	public void setup() throws SocketException {
		rtpSocket = new DatagramSocket(RTP_RCV_PORT);	// construct a new DatagramSocket to receive RTP packets from the server, on port RTP_RCV_PORT
		rtpSocket.setSoTimeout(5);						// set TimeOut value of the socket to 5msec.
	}

	public RTPpacket receivePacket() throws IOException {
		rcvdp = new DatagramPacket(buf, buf.length);							// Construct a DatagramPacket to receive data from the UDP socket
		rtpSocket.receive(rcvdp);												// receive the DP from the socket
		RTPpacket packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());	// create an RTPpacket object from the DP
		return packet;
	}

}