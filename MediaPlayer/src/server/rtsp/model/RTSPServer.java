package server.rtsp.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.rtsp.view.ServerView;

public class RTSPServer implements Runnable {

	private final ServerSocket socket;
	private final ExecutorService pool;

	public RTSPServer(int port) throws IOException {
		socket = new ServerSocket(port);
		pool = Executors.newCachedThreadPool();
		RTSPServer.log("started on port %d\n", port);
	}

	@Override
	public void run() {
		try {
			for (;;) {
				final RTSPRequest rtspRequest = new RTSPRequest(socket.accept());
		        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	ServerView view = new ServerView(rtspRequest);
		            	view.setVisible(true);
		            }
		        });
				pool.execute(rtspRequest);
			}
		} catch (IOException e) {
			pool.shutdown();
			RTSPServer.log("could not accept client on port %d\n", socket.getLocalPort());
		}
	}

	protected static void log(String format, Object... args) {
		System.out.printf("RTSP Server: " + format, args);
	}

}