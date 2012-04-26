package client.rtsp.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import client.statistics.StatisticsModel;

import server.rtsp.model.RTPpacket;


public class RTPTransport {

	public static int PORT = 25000;	// port where the client will receive the RTP packets

	private DatagramSocket socket;	// socket to be used to send and receive UDP packets
	private byte[] buffer;			// buffer used to store data received from the server

	public RTPTransport() {
		buffer = new byte[15000];	// allocate enough memory for the buffer used to receive data from the server
	}

	public void open() throws SocketException {
		socket = new DatagramSocket(PORT);	// construct a new DatagramSocket to receive RTP packets from the server, on port RTP_RCV_PORT
		socket.setSoTimeout(5);				// set TimeOut value of the socket to 5msec.
	}

	public void close() {
		socket.close();
	}

	public RTPpacket receivePacket(StatisticsModel statsLogger) throws IOException {
		DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length);	// Construct a DatagramPacket to receive data from the UDP socket
		socket.receive(dataPacket);												// receive the DP from the socket
		RTPpacket rtpPacket = new RTPpacket(dataPacket.getData(), dataPacket.getLength());		// create an RTPpacket object from the DP
		
		//TODO - check this is the best place to have this logging event
		statsLogger.logPacketReceived(rtpPacket, (int) System.currentTimeMillis());
		// print important header fields of the RTP packet received
//		System.out.printf("Got RTP packet with SeqNum #%d TimeStamp %d ms, of type %d\n",
//				rtpPacket.getSequenceNumber(), rtpPacket.getTimeStamp(), rtpPacket.getPayloadType());
//		rtpPacket.printheader();

		return rtpPacket;
	}

}