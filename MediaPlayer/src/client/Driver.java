package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import client.controller.MediaController;
import client.model.MediaPlayer;
import client.view.ClientView;


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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnknownHostException e) {
			System.err.printf("RTSP server host not found: %s\n", serverHost);
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error setting look and feel.", "Error", JOptionPane.ERROR_MESSAGE);
		}

		try {
			rtspServerPort = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			System.err.printf("RTSP server port must be an integer\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		MediaPlayer model = null;
		try {
			model = new MediaPlayer(videoName, serverIp, rtspServerPort);
		} catch (IOException e) {
			System.err.printf("I/O exception connecting to RTSP server: %s\n", e.getMessage());
			System.exit(0);
		}
		
		final MediaController controller = new MediaController(model);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClientView view = new ClientView(controller);
				view.setVisible(true);
			}
		});

	}

}