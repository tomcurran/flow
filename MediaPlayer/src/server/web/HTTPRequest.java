package server.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HTTPRequest implements Runnable {

	private final static String CRLF = "\r\n";
	private final static String DEFAULT_MIME_TYPE = "application/octet-stream";
	private static Map<String, String> extensionMIMETypes;

	static {
		extensionMIMETypes = new HashMap<String, String>();
		extensionMIMETypes.put("htm",  "text/html");
		extensionMIMETypes.put("html", "text/html");
		extensionMIMETypes.put("xml",  "text/xml");
		extensionMIMETypes.put("jpg",  "image/jpeg");
		extensionMIMETypes.put("jpeg", "image/jpeg");
		extensionMIMETypes.put("gif",  "image/gif");
		extensionMIMETypes.put("png",  "image/png");
		extensionMIMETypes.put("ico",  "image/x-icon");
		extensionMIMETypes.put("mjpeg",  "video/x-motion-jpeg");
	}

	private Socket socket;

	public HTTPRequest(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			processRequest();
		} catch (IOException e) {
			WebServer.log("I/O exception processing request (%s)\n", e.getMessage());
		}
	}

	private void processRequest() throws IOException {

		// Get a reference to the socket's input and output streams.
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		// Set up input stream filters.
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// Get the request line of the HTTP request message.
		String requestLine = br.readLine();
		// Display the request line.
		WebServer.log("request: %s\n", requestLine);

		// Get and display the header lines.
		String headerLine = null;
		while (((headerLine = br.readLine()) != null) && headerLine.length() != 0) {
			WebServer.log("request header: %s\n", headerLine);
		}

		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();
		// Prepend a "." so that file request is within the current directory.
		fileName = "./public" + fileName;

		// Open the requested file.
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			// HTTP-Version SP Status-Code SP Reason-Phrase CRLF
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
		} else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-type: text/html" + CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
					+ "<BODY>Not Found</BODY></HTML>";
		}

		// Send the status line.
		os.writeBytes(statusLine);
		// Send the content type line.
		os.writeBytes(contentTypeLine);
		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);

		// Send the entity body.
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			os.writeBytes(entityBody);
		}

		// Close streams and socket.
		os.close();
		br.close();
		socket.close();

	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws IOException {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;
		// Copy requested file into the socket's output stream.
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	private static String contentType(String fileName) {
		String ext = (fileName.substring(fileName.lastIndexOf('.') + 1)).toLowerCase();
		if (extensionMIMETypes.containsKey(ext)) {
			return extensionMIMETypes.get(ext);
		}
		return DEFAULT_MIME_TYPE;
	}

}