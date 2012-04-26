package client.model;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import client.model.MediaPlayer.STATE;
import client.model.MediaPlayer.Update;
import client.model.xmlparser.XMLParser;

public class Library extends Observable implements Observer {
	
	public enum Update {
		SWITCHPANEL,
		REFRESH, SELECTED
	}
	
	private Update state;
	private List<LibraryEntry> catalogue;
	private URL webserver;
	private URL metaFile;
	private final String metaFileExt = "/videos/database.xml";
	private XMLParser xmlParser;
	private String selected;
	
	public Library(InetAddress serverIp, int webServerPort){
		catalogue = new ArrayList<LibraryEntry>();
		try {
			System.out.println("server ip: " + serverIp.toString());
			metaFile = new URL("http", serverIp.getHostAddress(), webServerPort, metaFileExt);
			//metaFile = new URL(webserver, metaFileExt);
			xmlParser = new XMLParser(metaFile, serverIp.getHostAddress(), webServerPort);
			xmlParser.parseFile();
			catalogue = xmlParser.getLibrary();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Unable to establish connection with Web Server");
		}
		
	}

	@Override
	public void update(Observable o, Object arg) {
	}
	
	public void setState(Update state) {
		this.state = state;
		System.out.println("State: " + state.toString());
		this.setChanged();
		this.notifyObservers(state);
	}

	public Update getState(){
		return state;
	}
	
	public void refresh(String actionCommand) {
		xmlParser.parseFile();
		catalogue = xmlParser.getLibrary();
		if(actionCommand.endsWith("VIEW")){
			setState(state.SWITCHPANEL);
		}else{
			setState(state.REFRESH);
		}
	}
	
	public List<LibraryEntry> getLibrary(){
		return catalogue;
	}
	
	public String getSelectedMedia() {
		return selected;
	}

	public void setSelected(String media) {
		selected = media;
	}
}
