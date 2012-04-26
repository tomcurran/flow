package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import client.controller.LibraryController;
import client.model.Library;
import client.model.LibraryEntry;
import client.model.Library.Update;

public class LibraryView extends JPanel implements Observer, ListCellRenderer {

	private static final long serialVersionUID = 1L;
	private LibraryController controller;
	private boolean view;
	
	private JScrollPane scrollpane;
	private JList listView;
	private JList panelView;
	private List<Object> data;

	public LibraryView(LibraryController libraryController) {
		this.controller = libraryController;
		this.controller.getModel().addObserver(this);
		data = new ArrayList<Object>();
		initialiseComponents();
		initialiseListeners();
	}




	private void initialiseListeners() {
		 listView.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                if (e.getClickCount() == 2) {
	                	int index = listView.getSelectedIndex();
	                	JPanel temp = (JPanel) data.get(index);
	                	Component[] comps = temp.getComponents();
	                	JLabel label = (JLabel) comps[0];
	                	String media = label.getText();
	                	controller.getModel().setSelected(media);
	                	controller.getModel().setState(Update.SELECTED);
	                }
	            }
	        });
	}




	private void initialiseComponents() {
		
		this.setPreferredSize(new Dimension(400, 200));
		this.setLayout(new BorderLayout());
		scrollpane = new JScrollPane();
		listView = new JList();
		listView.setCellRenderer(this);
		panelView = new JList();
		panelView.setCellRenderer(this);
		panelView.setFixedCellHeight(100);
		panelView.setFixedCellWidth(133);
		panelView.setBorder(new EmptyBorder(10, 10, 10, 10));
		fillDisplay();
		scrollpane.getViewport().setView(listView);
		view = true;
		this.add(scrollpane, BorderLayout.CENTER);
		
	}



	private void fillDisplay() {
		data.clear();

		Library model = controller.getModel();
		List<LibraryEntry> entries = model.getLibrary();
		System.out.println("number of entries: " + entries.size());
		for (LibraryEntry libraryEntry : entries) {
			addMedia(libraryEntry);
		}
		
		listView.setListData(data.toArray());
		
		panelView.setLayoutOrientation(JList.VERTICAL_WRAP);
        panelView.setVisibleRowCount(-1);
		panelView.setListData(data.toArray());
		
	}



	@Override
	public void update(Observable o, Object arg) {
		switch ((Update) arg) {
		case SWITCHPANEL:
			switchPanel();
			break;
		case REFRESH:
			fillDisplay();
			this.repaint();
			break;
		default:
			break;
		}
	}
	
	private void switchPanel() {
		fillDisplay();
		if(view){
			scrollpane.getViewport().setView(listView);
			view = false;
		}else {
			scrollpane.getViewport().setView(panelView);
			view = true;
		}
		this.repaint();
	}




	//TODO Add thumbnail into this parameter set?
	private void addMedia(LibraryEntry entry) {
		
		
		//create icon
		Icon thumbnail = new ImageIcon(entry.getThumbnail());
		
		//create detail string
		String details = entry.getLocation();
		
		//create Jlabel made of Icon and Detail String
		Border border = LineBorder.createGrayLineBorder();
		JLabel listing = new JLabel(details, thumbnail, JLabel.CENTER);
		listing.setVerticalTextPosition(JLabel.BOTTOM);
		listing.setHorizontalTextPosition(JLabel.CENTER);
		listing.setBorder(border);
		
		//create panel
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(listing);
		
		//add panel to views
		data.add(panel);
		
	}


	@Override
	public Component getListCellRendererComponent(JList list, Object val,
			int index, boolean selected, boolean hasFocus) {
		if (val instanceof JPanel) {
			Component component = (Component) val;
        	component.setForeground (Color.white);
        	component.setBackground (selected ? Color.lightGray: Color.white);
			return component;
	    } else {
	      return new JLabel("???");
	    }
	}
	

}
