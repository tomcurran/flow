package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

public class RTSPTransport {

	private Socket socket;
	private static BufferedReader reader;
	private static BufferedWriter writer;
	private final static String CRLF = "\r\n";

	private ClientModel model;

	public RTSPTransport(ClientModel model, InetAddress ip, int port) throws IOException {
		this.model = model;
		socket = new Socket(ip, port);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	public int parseResponse() {
		int replyCode = 0;

		try {
			// parse status line and extract the reply_code:
			String statusLine = reader.readLine();
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

	public void sendRequest(String requestType) {
		try {
			writer.write(requestType + " " + model.getVideoName() + " " + "RTSP/1.0" + CRLF);
			writer.write("CSeq: " + model.getSequenceNumber() + CRLF);

			if (requestType.equals("SETUP")) {
				writer.write("Transport: RTP/UDP; client_port= " + RTPTransport.RTP_RCV_PORT + CRLF);
			} else {
				writer.write("Session: " + model.getSessionId() + CRLF);
			}

			writer.flush();
		} catch (IOException e) {
			System.out.println("I/O exception trying to send RTSP request: " + e.getMessage());
			System.exit(0);
		}
	}

}