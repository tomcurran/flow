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
			// TODO Should there be a separate model for the library of media?!
			// Would refresh the library list from the call model.refresh().
			// This would update the view with the latest list of available
			// media on the server. 
			model.refresh();
		} else if (action.equals("DOUBLECLICK")) {
			//TODO start playing the media. 
		}
	}
	

	public Library getModel() {
		return model;
	}

}
