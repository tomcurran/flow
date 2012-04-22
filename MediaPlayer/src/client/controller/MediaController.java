package client.controller;

import java.io.IOException;

import client.model.MediaPlayer;

public class MediaController {

	private MediaPlayer model;

	public MediaController(MediaPlayer model) {
		this.model = model;
	}

	public void playpause(String action) {
		if (action.equals("Play")) {
			try {
				model.play();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("Pause")) {
			model.pause();
		}
	}
	

	public MediaPlayer getModel() {
		return model;
	}

}