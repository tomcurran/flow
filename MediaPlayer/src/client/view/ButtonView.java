package client.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;

import client.controller.MediaController;
import client.model.MediaPlayer.Update;
import client.view.icons.*;

@SuppressWarnings("serial")
public class ButtonView extends JPanel implements Observer {
	
	private JButton playpauseButton;
	private JButton stopButton;
	private JButton seekbackButton;
	private JButton seekforwardButton;
	private JButton statsButton;

	private MediaController controller;
	
	private JPanel buttons;
    private ButtonGroup bg; 

	public ButtonView(MediaController controller) {
		this.controller = controller;
		this.controller.getModel().addObserver(this);
		initialiseComponents();
		updateGUI();
		initialiseListeners();
	}

	private void initialiseComponents() {
		
        buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(400, 40));
        
		
		playpauseButton = new JButton(new PlayIcon());
		playpauseButton.setPreferredSize(new Dimension(30, 30));
		playpauseButton.setActionCommand("PLAY");
		
		stopButton = new JButton(new StopIcon());
		stopButton.setPreferredSize(new Dimension(30, 30));
		stopButton.setActionCommand("STOP");
		
		seekbackButton = new JButton(new SeekBackIcon());
		seekbackButton.setPreferredSize(new Dimension(30, 30));
		seekbackButton.setActionCommand("BACKSEEK");
		
		seekforwardButton = new JButton(new SeekForwardIcon());
		seekforwardButton.setPreferredSize(new Dimension(30, 30));
		seekforwardButton.setActionCommand("FORWARDSEEK");
		
		statsButton = new JButton(new StatisticsIcon());
		statsButton.setPreferredSize(new Dimension(30, 30));
		statsButton.setActionCommand("STATS");
		
		bg = new ButtonGroup();
		
		bg.add(playpauseButton);
		bg.add(stopButton);
		bg.add(seekbackButton);
		bg.add(seekforwardButton);
		bg.add(statsButton);
		
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
				controller.playpause(playpauseButton.getActionCommand());
				System.out.println("command: " + playpauseButton.getActionCommand());
			}
		});

	}

	private void updateGUI() {
		switch (this.controller.getModel().getState()) {
		case STOP:
		case PAUSE:
			playpauseButton.setIcon(new PlayIcon());
			playpauseButton.setActionCommand("PLAY");
			System.out.println("switched to play");
			break;
		case BUFFER:
		case PLAY:
			playpauseButton.setIcon(new PauseIcon());
			playpauseButton.setActionCommand("PAUSE");
			System.out.println("switched to pause");
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("Arg: " +arg);
		switch ((Update) arg) {
		case STATE:
			updateGUI();
			break;
		default:
			break;
		}
	}

}