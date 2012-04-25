package client.controller;

import java.io.IOException;
import client.model.Library;
import client.model.MediaPlayer;

public class LibraryController {

	private Library model;

	public LibraryController(Library model) {
		this.model = model;
	}

	public void updateLibrary(String action) {
		if (action.equals("REFRESH")) { 
			model.refresh();
		} else if (action.equals("DOUBLECLICK")) {
			//TODO start playing the media. 
		}
	}
	

	public Library getModel() {
		return model;
	}

}
