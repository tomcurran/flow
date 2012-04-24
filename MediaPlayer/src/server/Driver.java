package server;

public class Driver {

	private static final String USEAGE = "[Web server listening port] [RTSP server listening port]";

	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.printf("Web server listening port and RTSP server listening port required\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		int webServerPort = -1;
		try {
			webServerPort = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.err.printf("Web server port must be an integer\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		int rtspServerPort = -1;
		try {
			rtspServerPort = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			System.err.printf("RTSP server port must be an integer\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		MediaServer server = new MediaServer(webServerPort, rtspServerPort);
		server.startWeb();
		server.startRTSP();
	}

}