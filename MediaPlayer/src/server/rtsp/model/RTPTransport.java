package server.rtsp.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class RTPTransport {

	private static DatagramSocket socket;	// socket to be used to send and receive UDP packets

	private InetAddress clientIp;			// Client IP address
	private int clientPort;					// destination port for RTP packets (given by the RTSP Client)

	public RTPTransport() {
		clientIp = null;
		clientPort = -1;
	}

	public static void open() throws SocketException {
		if (socket == null) {
			socket = new DatagramSocket();
		}
	}

	public static void close() {
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public void send(byte[] packetBits, int packetLength) throws IOException {
		// send the packet as a DatagramPacket over the UDP socket
		DatagramPacket sendPacket = new DatagramPacket(packetBits, packetLength, clientIp, clientPort);
		socket.send(sendPacket);
		// print the header bitstream
//		rtpPacket.printheader();
	}

	public void setClientIp(InetAddress inetAddress) {
		clientIp = inetAddress;
	}

	public void setClientPort(int parseInt) {
		clientPort = parseInt;
	}

}