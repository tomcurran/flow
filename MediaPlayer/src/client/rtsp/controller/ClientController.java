package client.rtsp.controller;

import java.io.IOException;
import java.net.SocketException;

import client.rtsp.model.ClientModel;

public class ClientController {

	private ClientModel model;

	public ClientController(ClientModel model) {
		this.model = model;
	}

	public void setup() {
		try {
			int responseCode = model.setup();
			switch (responseCode) {
			case 0:
				System.out.println("Invalid state to enter");
				break;
			case 200:
				break;
			default:
				System.out.println("Invalid Server Response");
				break;
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
			int responseCode = model.play();
			switch (responseCode) {
			case 0:
				System.out.println("Invalid state to enter");
				break;
			case 200:
				break;
			default:
				System.out.println("Invalid Server Response");
				break;
			}
		} catch (IOException e) {
			System.out.println("I/O exception trying to play RTSP request: " + e.getMessage());
		}
	}

	public void pause() {
		try {
			int responseCode = model.pause();
			switch (responseCode) {
			case 0:
				System.out.println("Invalid state to enter");
				break;
			case 200:
				break;
			default:
				System.out.println("Invalid Server Response");
				break;
			}
		} catch (IOException e) {
			System.out.println("I/O exception trying to play RTSP request: " + e.getMessage());
		}
	}

	public void tear() {
		try {
			int responseCode = model.tear();
			switch (responseCode) {
			case 0:
				System.out.println("Invalid state to enter");
				break;
			case 200:
				System.exit(0);
				break;
			default:
				System.out.println("Invalid Server Response");
				break;
			}
		} catch (IOException e) {
			System.out.println("I/O exception trying to play RTSP request: " + e.getMessage());
		}
	}

	public ClientModel getModel() {
		return model;
	}

}