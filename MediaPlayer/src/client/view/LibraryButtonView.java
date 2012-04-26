package client.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import client.controller.LibraryController;

public class LibraryButtonView extends JPanel implements Observer{
	//Panel and group that contains the buttons. 
	private JPanel buttons;
	private ButtonGroup bg;
		
    //The buttons. 
	private JButton panelviewButton;
	private JButton detailviewButton;
	private JButton statsButton;
	
	//The Icons.
	private ImageIcon panelIcon;
	private ImageIcon detailIcon;
	private ImageIcon statsIcon;
	
	//The controller.
	private LibraryController controller;

	public LibraryButtonView(LibraryController controller) {
		this.controller = controller;
		this.controller.getModel().addObserver(this);
		initialiseComponents();
		updateGUI();
		initialiseListeners();
	}
	
	private void initialiseComponents() {
		
		//Create JPanel for buttons. 
		buttons = new JPanel();
		buttons.setPreferredSize(new Dimension(300, 40));
		
		//Setup Icons.
		panelIcon = new ImageIcon("images/icons/panelview.png");
		detailIcon = new ImageIcon("images/icons/detailview/png");
		statsIcon = new ImageIcon("images/icons/fishes.png");
		
		//SetupButtons.
		panelviewButton = new JButton(panelIcon);
		panelviewButton.setPreferredSize(new Dimension(30, 30));
		panelviewButton.setActionCommand("PANELVIEW");
		
		detailviewButton = new JButton(detailIcon);
		detailviewButton.setPreferredSize(new Dimension(30, 30));
		detailviewButton.setActionCommand("DETAILVIEW");
		
		statsButton = new JButton(statsIcon);
		statsButton.setPreferredSize(new Dimension(30, 30));
		statsButton.setActionCommand("STATS");
		
		//Setup the ButtonGroup.
		bg = new ButtonGroup();
		
		bg.add(panelviewButton);
		bg.add(detailviewButton);
		bg.add(statsButton);
		
		//Add buttons to panel.
		buttons.add(panelviewButton);
		buttons.add(detailviewButton);
		buttons.add(statsButton);
		
		//add to this panel.
		super.add(buttons);
		
	}
	
	private void initialiseListeners() {
		
		panelviewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.switchview(panelviewButton.getActionCommand());
			}
		});
		
		detailviewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.switchview(detailviewButton.getActionCommand());
			}
		});
		
		statsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO How should we handle sliding in/out the stats panel? 
			}
		});
		
	}

	private void updateGUI() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
}
