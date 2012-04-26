package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import client.controller.LibraryController;
import client.model.Library;
import client.model.LibraryEntry;
import client.model.Update;

public class LibraryView extends JPanel implements Observer, ListCellRenderer {

	private static final long serialVersionUID = 1L;
	private LibraryController controller;
	private String currentView = "listView";

	private JScrollPane scrollpane;
	private JList listView;
	private JList panelView;
	private List<Object> dataList;
	private List<Object> dataPanel;

	public LibraryView(LibraryController libraryController) {
		this.controller = libraryController;
		this.controller.getModel().addObserver(this);
		dataList = new ArrayList<Object>();
		dataPanel = new ArrayList<Object>();
		initialiseComponents();
		initialiseListeners();
	}

	private void initialiseListeners() {
		listView.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				int index = listView.getSelectedIndex();
				JPanel temp = (JPanel) dataPanel.get(index);
				Component[] comps = temp.getComponents();
				JLabel label = (JLabel) comps[0];
				String media = label.getText();
				controller.getModel().setSelected(media);
				controller.getModel().setState(Update.SELECTED);

			}
		});

		panelView.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				int index = panelView.getSelectedIndex();
				JPanel temp = (JPanel) dataPanel.get(index);
				Component[] comps = temp.getComponents();
				JLabel label = (JLabel) comps[0];
				String media = label.getText();
				controller.getModel().setSelected(media);
				controller.getModel().setState(Update.SELECTED);

			}
		});
	}

	private void initialiseComponents() {

		this.setPreferredSize(new Dimension(340, 340));
		this.setLayout(new BorderLayout());
		scrollpane = new JScrollPane();
		scrollpane.setBackground(Color.GREEN);
		this.setBackground(Color.BLACK);
		listView = new JList();
		listView.setCellRenderer(this);
		listView.setBackground(new Color(7, 54, 56));
		panelView = new JList();

		panelView.setCellRenderer(this);
		panelView.setFixedCellHeight(90);
		panelView.setFixedCellWidth(100);
		panelView.setBorder(new EmptyBorder(10, 10, 10, 10));
		panelView.setBackground(new Color(7, 54, 56));
		fillDisplay();
		scrollpane.getViewport().setView(listView);
		scrollpane.setPreferredSize(new Dimension(340, 340));
		this.add(scrollpane, BorderLayout.CENTER);

	}

	private void fillDisplay() {
		dataList.clear();
		dataPanel.clear();

		Library model = controller.getModel();
		List<LibraryEntry> entries = model.getLibrary();
		System.out.println("number of entries: " + entries.size());
		for (LibraryEntry libraryEntry : entries) {
			addMedia(libraryEntry);
		}

		listView.setListData(dataList.toArray());

		panelView.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		panelView.setVisibleRowCount(-1);
		panelView.setListData(dataPanel.toArray());

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
		if (currentView.startsWith("panel")) {
			scrollpane.getViewport().setView(listView);
			currentView = "listView";
		} else {
			scrollpane.getViewport().setView(panelView);
			currentView = "panelView";
		}
		this.repaint();
	}

	// TODO Add thumbnail into this parameter set?
	private void addMedia(LibraryEntry entry) {

		// create icon
		Icon thumbnail = new ImageIcon(entry.getThumbnail());

		// create panel string
		String panelString = entry.getLocation();

		// create detailViewString
		String detailString = "Location: " + entry.getLocation()
				+ "\n Period: " + entry.getPeriod() + "\n Type: "
				+ entry.getType();

		// create Jlabel made of Icon and Detail String
		Border border = LineBorder.createGrayLineBorder();
		JLabel panelLabel = new JLabel(panelString, thumbnail, JLabel.CENTER);
	
		panelLabel.setVerticalTextPosition(JLabel.BOTTOM);
		panelLabel.setHorizontalTextPosition(JLabel.CENTER);
		panelLabel.setBorder(border);

		JLabel detailLabel = new JLabel(detailString, thumbnail, JLabel.CENTER);
		Border paddingBorder = BorderFactory.createEmptyBorder(5,5,5,5);
		Border detailborder = BorderFactory.createLineBorder(new Color(7, 54, 56));
		
		detailLabel.setBorder(BorderFactory.createCompoundBorder(detailborder,paddingBorder));
		

		detailLabel.setSize(new Dimension(330, 100));
	

		// create panel
		JPanel panelPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelPane.add(panelLabel);

		JPanel detailPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		detailPane.add(detailLabel);

		// add panel to views
		dataPanel.add(panelPane);

		dataList.add(detailPane);

	}

	@Override
	public Component getListCellRendererComponent(JList list, Object val,
			int index, boolean selected, boolean hasFocus) {
		if (val instanceof JPanel) {
			Component component = (Component) val;
			component.setForeground(Color.lightGray);
			component
					.setBackground(selected ? new Color(88, 110, 117) : Color.lightGray);
			return component;
		} else {
			return new JLabel("???");
		}
	}

}
