package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;


public class Driver {

	private static final String USEAGE = "java Client [Server hostname] [Server RTSP listening port] [Video file requested]";

	public static void main(String[] args) {

		if (args.length != 3) {
			System.err.printf("RTSP server host name, listening port and video file required\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		String serverHost = args[0];
		InetAddress serverIp = null;
		int rtspServerPort = 0;
		String videoName = args[2];

		try {
			serverIp = InetAddress.getByName(serverHost);
		} catch (UnknownHostException e) {
			System.err.printf("RTSP server host not found: %s\n", serverHost);
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		try {
			rtspServerPort = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			System.err.printf("RTSP server port must be an integer\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		final ClientModel model = new ClientModel(videoName);
		final ClientController controller = new ClientController(model, serverIp, rtspServerPort);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClientView view = new ClientView(model, controller);
				view.setVisible(true);
//				ClientView view2 = new ClientView(model, controller);
//				view2.setVisible(true);
			}
		});

	}

}