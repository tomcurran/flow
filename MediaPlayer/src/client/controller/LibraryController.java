package client.controller;

import java.io.IOException;
import client.model.Library;
import client.model.MediaPlayer;

public class LibraryController {

	private Library model;

	public LibraryController(Library model) {
		this.model = model;
	}

	public void updateLibrary(String actionCommand) {
		if (actionCommand.equals("REFRESH")) { 
			model.refresh(actionCommand);
		} else if (actionCommand.equals("DOUBLECLICK")) {
			//TODO start playing the media. 
		}
	}
	

	public Library getModel() {
		return model;
	}

	public void switchview(String actionCommand) {
		model.refresh(actionCommand);
	}

}
