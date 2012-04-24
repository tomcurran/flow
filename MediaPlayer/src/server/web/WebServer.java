package server.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer implements Runnable {

	private final ServerSocket socket;
	private final ExecutorService pool;

	public WebServer(int port) throws IOException {
		socket = new ServerSocket(port);
		pool = Executors.newCachedThreadPool();
		WebServer.log("started on port %d\n", port);
	}

	public void run() {
		try {
			for (;;) {
				pool.execute(new HTTPRequest(socket.accept()));
			}
		} catch (IOException e) {
			pool.shutdown();
			WebServer.log("Could not accept client on port %d\n", socket.getLocalPort());
		}
	}

	protected static void log(String format, Object... args) {
		System.out.printf("Web Server: " + format, args);
	}

}