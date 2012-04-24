package server;

public class Driver {

	private static final String USEAGE = "[Web server listening port]";

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.printf("Web server listening port required\n");
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

		MediaServer server = new MediaServer(webServerPort);
		server.startWeb();
	}

}