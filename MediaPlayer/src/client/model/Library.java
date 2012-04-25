package client.model;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import client.model.xmlparser.XMLParser;

public class Library extends Observable implements Observer {
	
	private List<LibraryEntry> catalogue;
	private URL webserver;
	private URL metaFile;
	private final String metaFileExt = "/videos/database.xml";
	private XMLParser xmlParser;
	
	public Library(InetAddress serverIp, int webServerPort){
		catalogue = new ArrayList<LibraryEntry>();
		try {
			System.out.println("server ip: " + serverIp.toString());
			metaFile = new URL("http", serverIp.getHostAddress(), webServerPort, metaFileExt);
			//metaFile = new URL(webserver, metaFileExt);
			xmlParser = new XMLParser(metaFile);
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

	public void refresh() {
		xmlParser.parseFile();
		catalogue = xmlParser.getLibrary();
		this.setChanged();
		this.notifyObservers("REFRESHED");
	}
	
	public List<LibraryEntry> getLibrary(){
		return catalogue;
	}
	
	public String getSelectedMedia() {
		// TODO Auto-generated method stub
		return null;
	}
}
