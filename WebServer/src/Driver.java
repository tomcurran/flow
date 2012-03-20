import java.io.IOException;

public class Driver {

	public static void main(String[] args) {
		try {
			new Server().run();
		} catch (IOException e) {
			System.out.printf("Could not start server to listen on port: %d\n", Server.PORT);
		}
	}

}