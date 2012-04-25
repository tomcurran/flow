package client.model.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import client.model.Library;
import client.rtsp.model.ClientModel;
import client.rtsp.model.RTPTransport;
import client.rtsp.model.ClientModel.RTSP_STATE;

public class HTTPHandler {
	
	private final static String CRLF = "\r\n";
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private InetAddress ip;
	private int port;
	private Library model;
	
	public HTTPHandler(Library model, InetAddress ip, int port) {
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
		System.out.println("HTTP Response - Received from Server:");
		System.out.println(statusLine);

		StringTokenizer tokens = new StringTokenizer(statusLine);
		tokens.nextToken();
		replyCode = Integer.parseInt(tokens.nextToken());

		// if reply code is OK get and print the 2 other lines
		if (replyCode == 200) {
			String seqNumLine = reader.readLine();
			System.out.println(seqNumLine);

			String sessionLine = reader.readLine();
			System.out.println(sessionLine);
		}
		if()

		return replyCode;
	}
	
	public void sendRequest(String requestType, String resource) throws IOException {
		writer.write(requestType + " " + resource + " " + "HTTP/1.0" + CRLF);
		writer.flush();
	}
}
