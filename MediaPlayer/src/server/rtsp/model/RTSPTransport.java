package server.rtsp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import server.rtsp.model.RTSPRequest.RTSP_METHODS;

public class RTSPTransport {

	private final static String CRLF = "\r\n";

	private Socket rtspSocket;					// socket used to send/receive RTSP messages	private Socket rtspSocket;					// socket used to send/receive RTSP messages
	private BufferedReader rtspReader;	// input stream filters
	private BufferedWriter rtspWriter;	// output stream filters
	private RTSPRequest model;

	public RTSPTransport(RTSPRequest model, Socket socket) {
		this.model = model;
		this.rtspSocket = socket;
	}

	public void open() throws IOException {
		rtspReader = new BufferedReader(new InputStreamReader(rtspSocket.getInputStream()));
		rtspWriter = new BufferedWriter(new OutputStreamWriter(rtspSocket.getOutputStream()));
	}

	public InetAddress getClientIp() {
		return rtspSocket.getInetAddress();
	}

	public int getClientPort() {
		return rtspSocket.getPort();
	}

	public void close() throws IOException {
		rtspSocket.close();
	}

	public RTSP_METHODS parseRequest() {
		RTSP_METHODS request = RTSP_METHODS.NONE;
		try {
			// parse request line and extract the request_type:
			String requestLine = rtspReader.readLine();
			if (requestLine == null) {
				return request;
			}
//			RTSPServer.log("received from client... \n");
			RTSPServer.log("%s (%d)\n", requestLine, model.getSessionId());

			StringTokenizer tokens = new StringTokenizer(requestLine);
			String requestTypeString = new String(tokens.nextToken());

			// convert to request_type structure:
			if (requestTypeString.compareTo("SETUP") == 0) {
				request = RTSP_METHODS.SETUP;
			} else if (requestTypeString.compareTo("PLAY") == 0) {
				request = RTSP_METHODS.PLAY;
			} else if (requestTypeString.compareTo("PAUSE") == 0) {
				request = RTSP_METHODS.PAUSE;
			} else if (requestTypeString.compareTo("TEARDOWN") == 0) {
				request = RTSP_METHODS.TEARDOWN;
			}

			if (request == RTSP_METHODS.SETUP) {
				// extract videoName from requestLine
				model.setVideoName(tokens.nextToken());
			}

			// parse the seqNumLine and extract CSeq field
			String seqNumLine = rtspReader.readLine();
//			RTSPServer.log("%s\n", seqNumLine);
			tokens = new StringTokenizer(seqNumLine);
			tokens.nextToken();
			model.setSequenceNumber(Integer.parseInt(tokens.nextToken()));

			// get lastLine
			String lastLine = rtspReader.readLine();
//			RTSPServer.log("%s\n", lastLine);

			if (request == RTSP_METHODS.SETUP) {
				// extract rtpDestPort from lastLine
				tokens = new StringTokenizer(lastLine);
				for (int i = 0; i < 3; i++) {
					tokens.nextToken(); // skip unused stuff
				}
				model.setClientPort(Integer.parseInt(tokens.nextToken()));
			}
			// else lastLine will be the SessionId line ... do not check for now.
		} catch (IOException e) {
			RTSPServer.log("I/O exception parsing RTSP request: %s\n", e.getMessage());
			request = RTSP_METHODS.NONE;
		}
		return request;
	}

	public void sendResponse() {
		try {
			rtspWriter.write("RTSP/1.0 200 OK" + CRLF);
			rtspWriter.write("CSeq: " + model.getSquenceNumber() + CRLF);
			rtspWriter.write("Session: " + model.getSessionId() + CRLF);
			rtspWriter.flush();
//			RTSPServer.log("sending response to client\n");
		} catch (IOException e) {
			RTSPServer.log("I/O exception sending RTSP response: %s\n", e.getMessage());
		}
	}

}