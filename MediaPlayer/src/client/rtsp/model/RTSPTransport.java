package client.rtsp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import client.rtsp.model.ClientModel.RTSP_STATE;

public class RTSPTransport {

	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private final static String CRLF = "\r\n";

	private ClientModel model;
	private InetAddress ip;
	private int port;

	public RTSPTransport(ClientModel model, InetAddress ip, int port) {
		this.model = model;
		this.ip = ip;
		this.port = port;
	}

	public void open() throws IOException {
		socket = new Socket(ip, port);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	public void close() throws IOException {
		reader.close();
		writer.close();
		socket.close();
	}

	public int parseResponse() throws IOException {
		int replyCode = 0;

		// parse status line and extract the replyCode
		String statusLine = reader.readLine();
		if (statusLine == null) {
			close();
			return replyCode;
		}
		System.out.println("RTSP Client - Received from Server:");
		System.out.println(statusLine);

		StringTokenizer tokens = new StringTokenizer(statusLine);
		tokens.nextToken(); // skip over the RTSP version
		replyCode = Integer.parseInt(tokens.nextToken());

		// if reply code is OK get and print the 2 other lines
		if (replyCode == 200) {
			String seqNumLine = reader.readLine();
			System.out.println(seqNumLine);

			String sessionLine = reader.readLine();
			System.out.println(sessionLine);

			// if state == INIT gets the Session Id from the SessionLine
			if (model.getState() == RTSP_STATE.INIT) {
				tokens = new StringTokenizer(sessionLine);
				tokens.nextToken(); // skip over the Session:
				model.setSessionId(Integer.parseInt(tokens.nextToken()));
			}
		}

		return replyCode;
	}

	public void sendRequest(String requestType) throws IOException {
		writer.write(requestType + " " + model.getVideoName() + " " + "RTSP/1.0" + CRLF);
		writer.write("CSeq: " + model.getSequenceNumber() + CRLF);

		if (requestType.equals("SETUP")) {
			writer.write("Transport: RTP/UDP; client_port= " + RTPTransport.PORT + CRLF);
		} else {
			writer.write("Session: " + model.getSessionId() + CRLF);
		}

		writer.flush();
	}

}