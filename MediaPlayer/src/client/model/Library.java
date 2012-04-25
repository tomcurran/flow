package client.model;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Library extends Observable implements Observer {
	
	private List<LibraryEntry> catalogue;
	private Socket socket;
	
	
	public Library(InetAddress serverIp, int webServerPort){
		//TODO Initialise the data structures for holding the xml.
		catalogue = new ArrayList<LibraryEntry>();
	}

	@Override
	public void update(Observable o, Object arg) {
	}

	public void refresh() {
		//TODO Update the library to represent the current state of the 
		//     database. 
		this.setChanged();
		this.notifyObservers("REFRESHED");
	}
	
	public List<LibraryEntry> getLibrary(){
		return catalogue;
	}
	
	private class HTTPRequestBuilder(){
		
	}

}

	public String getSelectedMedia() {
		// TODO Auto-generated method stub
		return null;
	}
