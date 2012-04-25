package client.view;

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
	private JButton thumnnailviewButton;
	private JButton plainviewButton;
	private JButton statsButton;
	
	//The Icons.
	private ImageIcon thumbnailIcon;
	private ImageIcon plainIcon;
	private ImageIcon statsIcon;
	
	//The controller.
	private LibraryController controller;

	public LibraryButtonView(LibraryController controller) {
		this.controller = controller;
		this.controller.getModel().addObserver(this);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
}
