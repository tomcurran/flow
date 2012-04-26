package client.controller;

import java.io.IOException;

import client.model.MediaPlayer;
import client.model.MediaPlayer.STATE;

public class MediaController {

	private MediaPlayer model;

	public MediaController(MediaPlayer model) {
		this.model = model;
	}

	public void playpause(String action) {
		if (action.equals("PLAY")) {
			try {
				model.play();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("PAUSE")) {
			model.pause();
		} else if (action.equals("STOP")) {
			model.stopPlaying();
			model.setState(STATE.STOP);
		}
	}
	

	public MediaPlayer getModel() {
		return model;
	}

}