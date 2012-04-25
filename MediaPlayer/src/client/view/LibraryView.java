package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import client.controller.LibraryController;
import client.model.Library;
import client.model.LibraryEntry;

public class LibraryView extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private LibraryController controller;
	
	private JScrollPane scrollpane;
	private JList listView;
	private JList panelView;
	private List<Object> data;

	public LibraryView(LibraryController libraryController) {
		this.controller = libraryController;
		this.controller.getModel().addObserver(this);
		data = new ArrayList<Object>();
		initialiseComponents();
	}



	private void initialiseComponents() {
		this.setPreferredSize(new Dimension(400, 400));
		this.setLayout(new BorderLayout());
		scrollpane = new JScrollPane(listView);
		scrollpane.setEnabled(true);
		listView = new JList();
		listView.setPreferredSize(new Dimension(400, 400));
		listView.setMinimumSize(new Dimension(400, 400));
		panelView = new JList();
		scrollpane.setSize(new Dimension(1000, 1000));
		fillDisplay();
		scrollpane.getViewport().setView(listView);
		this.add(scrollpane, BorderLayout.CENTER);
	}



	private void fillDisplay() {
		Library model = controller.getModel();
		List<LibraryEntry> entries = model.getLibrary();
		
		System.out.println("number of entries: " + entries.size());
		for (LibraryEntry libraryEntry : entries) {
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
			addMedia(libraryEntry.getLocation(), libraryEntry.getRunTime(), libraryEntry.getSize());
		}
		
		listView = new JList(data.toArray());
		
	}



	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
	
	//TODO Add thumbnail into this parameter set?
	private void addMedia(String title, String runningTime, String fileSize) {
		
		JPanel mediaPanel = new JPanel();
		
		data.add(new ImageIcon("./images/icons/forward.png") + " " + title);
		
		JLabel mediaTitle = new JLabel(title); 
	
		JLabel mediaTime = new JLabel("Running Time: " + runningTime);
		JLabel mediaSize = new JLabel(fileSize);
		System.out.println("mediaTitle: " + title);
		listView.add(mediaTitle);
		listView.add(mediaTime);
		listView.add(mediaSize);
	
		
		
	}

}
