package server;

import java.io.IOException;

import server.rtsp.model.RTSPServer;
import server.web.WebServer;

public class MediaServer {

	private int webServerPort;
	private int rtspServerPort;

	public MediaServer(int webServerPort, int rtspServerPort) {
		this.webServerPort = webServerPort;
		this.rtspServerPort = rtspServerPort;
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
		try {
			new Thread(new RTSPServer(rtspServerPort)).start();
		} catch (IOException e) {
			MediaServer.log("could not start RTSP server on port %d\n", rtspServerPort);
		}
	}

	protected static void log(String format, Object... args) {
		System.out.printf("Media Server: " + format, args);
	}

}