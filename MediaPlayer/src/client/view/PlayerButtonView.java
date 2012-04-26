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

import client.controller.MediaController;
import client.model.MediaPlayer.STATE;
import client.model.Update;

@SuppressWarnings("serial")
public class PlayerButtonView extends JPanel implements Observer {
	
	//Panel and group that contains the buttons. 
	private JPanel buttons;
    private ButtonGroup bg;
	
    //The buttons. 
	private JButton playpauseButton;
	private JButton stopButton;
	private JButton seekbackButton;
	private JButton seekforwardButton;
	private JButton statsButton;
	private JButton returntoLibrary;
    
	//The icons.
	private ImageIcon playIcon;
	private ImageIcon pauseIcon;
	private ImageIcon stopIcon;
	private ImageIcon rewindIcon;
	private ImageIcon fastForwardIcon;
	private ImageIcon statsIcon;
	private ImageIcon returntoLibraryIcon;
	
	//The controller the buttons interact with. 
	private MediaController controller;
	
	private ClientView mainView;

	public PlayerButtonView(MediaController controller, ClientView mainView) {
		this.controller = controller;
		this.controller.getModel().addObserver(this);
		this.mainView = mainView;
		initialiseComponents();
		updateGUI();
		initialiseListeners();
	}

	private void initialiseComponents() {
		
        buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(400, 40));
        
        playIcon = new ImageIcon("images/icons/play.png");
        pauseIcon = new ImageIcon("images/icons/pause.png");
		playpauseButton = new JButton(playIcon);
		playpauseButton.setPreferredSize(new Dimension(30, 30));
		playpauseButton.setActionCommand("PLAY");

        stopIcon = new ImageIcon("images/icons/stop.png");
		stopButton = new JButton(stopIcon);
		stopButton.setPreferredSize(new Dimension(30, 30));
		stopButton.setActionCommand("STOP");

        rewindIcon = new ImageIcon("images/icons/rewind.png");
		seekbackButton = new JButton(rewindIcon);
		seekbackButton.setPreferredSize(new Dimension(30, 30));
		seekbackButton.setActionCommand("BACKSEEK");

        fastForwardIcon = new ImageIcon("images/icons/forward.png");
		seekforwardButton = new JButton(fastForwardIcon);
		seekforwardButton.setPreferredSize(new Dimension(30, 30));
		seekforwardButton.setActionCommand("FORWARDSEEK");

        statsIcon = new ImageIcon("images/icons/fishes.png");
		statsButton = new JButton(statsIcon);
		statsButton.setPreferredSize(new Dimension(30, 30));
		statsButton.setActionCommand("STATS");
		
		returntoLibraryIcon = new ImageIcon("images/icons/library.png");
		returntoLibrary = new JButton(returntoLibraryIcon);
		returntoLibrary.setPreferredSize(new Dimension(30, 30));
		returntoLibrary.setActionCommand("RETURN");
		
		bg = new ButtonGroup();
		
		bg.add(returntoLibrary);
		bg.add(playpauseButton);
		bg.add(stopButton);
		bg.add(seekbackButton);
		bg.add(seekforwardButton);
		bg.add(statsButton);
		
		buttons.add(returntoLibrary);
		buttons.add(playpauseButton);
		buttons.add(stopButton);
		buttons.add(seekbackButton);
		buttons.add(seekforwardButton);
		buttons.add(statsButton);
		
		super.add(buttons);
		
	}

	private void initialiseListeners() {

		playpauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("!!!!PLAYPAUSE CLICKED!!!!");
				controller.playpause(playpauseButton.getActionCommand());
				
			}
		});
		
		statsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainView.setStats();
			}
		});
		
		returntoLibrary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.getModel().setState(STATE.RETURN);
				
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.playpause(stopButton.getActionCommand());
			}
		});

	}

	private void updateGUI() {
		System.out.println("Changed!!!!!!!!!!!!!!!!");
		switch (this.controller.getModel().getState()) {
		case STOP:
		case PAUSE:
			
			playpauseButton.setIcon(playIcon);
			playpauseButton.setActionCommand("PLAY");
			System.out.println("Set the actioncommand");
			break;
		case BUFFER:
		case PLAY:
			playpauseButton.setIcon(pauseIcon);
			playpauseButton.setActionCommand("PAUSE");
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((Update) arg) {
		case STATE:
			updateGUI();
			break;
		default:
			break;
		}
	}

}