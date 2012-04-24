package server.rtsp.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
				pool.execute(new RTSPRequest(socket.accept()));
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