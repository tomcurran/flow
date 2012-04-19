package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;

import javax.swing.Timer;

public class ClientController {

	private ClientModel model;
	private Timer timer; // timer used to receive data from the UDP socket

	public ClientController(ClientModel model) {
		this.model = model;
		timer = new Timer(20, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
	}

	public void setup() {
		try {
			int repsonseCode = model.setup();
			if (repsonseCode == 0) {
				System.out.println("Invalid state to enter");
			} else if (repsonseCode != 200) {
				System.out.println("Invalid Server Response");
			}
		} catch (SocketException e) {
			System.out.println("Socket exception: " + e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			System.out.println("I/O exception trying to setup RTSP request: " + e.getMessage());
		}
	}

	public void play() {
		try {
			int repsonseCode = model.play();
			if (repsonseCode == 0) {
				System.out.println("Invalid state to enter");
			} else if (repsonseCode != 200) {
				System.out.println("Invalid Server Response");
			} else {
				timer.start();
			}
		} catch (IOException e) {
			System.out.println("I/O exception trying to play RTSP request: " + e.getMessage());
		}
	}

	public void pause() {
		try {
			int repsonseCode = model.pause();
			if (repsonseCode == 0) {
				System.out.println("Invalid state to enter");
			} else if (repsonseCode != 200) {
				System.out.println("Invalid Server Response");
			} else {
				timer.stop();
			}
		} catch (IOException e) {
			System.out.println("I/O exception trying to play RTSP request: " + e.getMessage());
		}
	}

	public void tear() {
		try {
			int repsonseCode = model.tear();
			if (repsonseCode == 0) {
				System.out.println("Invalid state to enter");
			} else if (repsonseCode != 200) {
				System.out.println("Invalid Server Response");
			} else {
				timer.stop();
				System.exit(0);
			}
		} catch (IOException e) {
			System.out.println("I/O exception trying to play RTSP request: " + e.getMessage());
		}
	}

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				model.receivePacket();
			} catch (InterruptedIOException iioe) {
				// System.out.println("Nothing to read");
			} catch (IOException ioe) {
				System.out.println("Exception caught: " + ioe);
			}
		}
	}

	public ClientModel getModel() {
		return model;
	}

}