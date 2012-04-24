package server;

import java.io.IOException;

import server.web.WebServer;

public class MediaServer {

	private int webServerPort;

	public MediaServer(int webServerPort) {
		this.webServerPort = webServerPort;
	}

	public void startWeb() {
		MediaServer.log("starting web server\n");
		try {
			new Thread(new WebServer(webServerPort)).start();
		} catch (IOException e) {
			MediaServer.log("could not start web server on port %d\n", webServerPort);
		}
	}

	public void startRTSP() {
		MediaServer.log("starting RTSP server\n");
		// TODO rtsp server
	}

	protected static void log(String format, Object... args) {
		System.out.printf("Media Server: " + format, args);
	}

}