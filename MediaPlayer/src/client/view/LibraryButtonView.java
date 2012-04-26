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
		buttons.setPreferredSize(new Dimension(300, 35));
		
		//Setup Icons.
		panelIcon = new ImageIcon("images/icons/panelview.png");
		detailIcon = new ImageIcon("images/icons/detailview.png");
		statsIcon = new ImageIcon("images/icons/fishes.png");
		
		//SetupButtons.
		panelviewButton = new JButton(panelIcon);
		panelviewButton.setPreferredSize(new Dimension(30, 30));
		panelviewButton.setActionCommand("PANELVIEW");
	
		statsButton = new JButton(statsIcon);
		statsButton.setPreferredSize(new Dimension(30, 30));
		statsButton.setActionCommand("STATS");
		
		//Setup the ButtonGroup.
		bg = new ButtonGroup();
		
		bg.add(panelviewButton);
		bg.add(statsButton);
		
		//Add buttons to panel.
		buttons.add(panelviewButton);
		buttons.add(statsButton);
		
		//add to this panel.
		super.add(buttons);
		
	}
	
	private void initialiseListeners() {
		
		panelviewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.switchview(panelviewButton.getActionCommand());
				if(panelviewButton.getActionCommand().startsWith("PANEL")){
					panelviewButton.setIcon(detailIcon);
					panelviewButton.setActionCommand("DETAILVIEW");
				} else{
					panelviewButton.setIcon(panelIcon);
					panelviewButton.setActionCommand("PANELVIEW");
				}
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
	}

	

	@Override
	public void update(Observable arg0, Object arg1) {
	}
	
	
}
