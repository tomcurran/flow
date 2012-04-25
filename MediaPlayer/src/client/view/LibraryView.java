package client.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import client.controller.LibraryController;
import client.model.Library;
import client.model.LibraryEntry;

public class LibraryView extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private LibraryController controller;

	public LibraryView(LibraryController libraryController) {
		this.controller = libraryController;
		this.controller.getModel().addObserver(this);
		initialiseComponents();
	}



	private void initialiseComponents() {
		this.setPreferredSize(new Dimension(100, 100));
	
		fillDisplay();
	}



	private void fillDisplay() {
		Library model = controller.getModel();
		List<LibraryEntry> entries = model.getLibrary();
		
		System.out.println("number of entries: " + entries.size());
		for (LibraryEntry libraryEntry : entries) {
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
		}
	}



	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
	
	//TODO Add thumbnail into this parameter set?
	private void addMedia(String title, String runningTime, String fileSize) {
		
		JPanel mediaPanel = new JPanel();
		
		mediaPanel.setPreferredSize(new Dimension(100, 100));
		
		JLabel mediaTitle = new JLabel(); 
		mediaTitle.setText("moo");
		JLabel mediaTime = new JLabel("Running Time: " + runningTime);
		JLabel mediaSize = new JLabel(fileSize);
		System.out.println("mediaTitle: " + title);
		this.add(mediaTitle);
		mediaPanel.add(mediaTime);
		mediaPanel.add(mediaSize);
		
		this.add(mediaTitle);

	}

}
