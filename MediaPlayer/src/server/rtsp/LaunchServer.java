package server.rtsp;

import server.rtsp.model.ServerModel;
import server.rtsp.view.ServerView;

	/**
	 * @author jwb09119
	 * @date 18/04/2012
	 * 
	 * Main launcher class for the Server application.
	 */

public class LaunchServer {
	private static final String USEAGE = "java Server [RTSP listening port]";
	

	public static void main (String args[]) {
		if (args.length != 1) {
			System.err.printf("Server RTSP listening port required\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}

		// get RTSP socket port from the command line
		int rtspPort = -1;
		try {
			rtspPort = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.err.printf("Server RTSP listening port must be an integer\n");
			System.err.printf("Useage: %s\n", USEAGE);
			System.exit(0);
		}
		
		final ServerModel model = new ServerModel(rtspPort);
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	ServerView view = new ServerView(model);
            	view.showView();
            }
        });
		ServerView view = new ServerView(model);
		
		model.addObserver(view);
		
		model.startServer();
	}
}
