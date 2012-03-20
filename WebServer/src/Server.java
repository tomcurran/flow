import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	public static final int PORT = 6789;

	private final ServerSocket socket;
	private final ExecutorService pool;

	public Server() throws IOException {
		socket = new ServerSocket(Server.PORT);
		pool = Executors.newCachedThreadPool();
		System.out.printf("Server started on port: %d\n", Server.PORT);
	}

	public void run() {
		try {
			for (;;) {
				pool.execute(new HTTPRequest(socket.accept()));
			}
		} catch (IOException e) {
			pool.shutdown();
			System.out.printf("Could not accept client on port: %d\n", Server.PORT);
		}
	}

}