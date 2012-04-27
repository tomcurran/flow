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
import client.model.Update;

@SuppressWarnings("serial")
public class ButtonView extends JPanel implements Observer {

	private JButton playpauseButton;
	private JButton stopButton;
	private JButton seekbackButton;
	private JButton seekforwardButton;
	private JButton statsButton;
	private JPanel buttons;
    private ButtonGroup bg;
	private ImageIcon playIcon;
	private ImageIcon pauseIcon;
	private ImageIcon stopIcon;
	private ImageIcon rewindIcon;
	private ImageIcon fastForwardIcon;
	private ImageIcon statsIcon;
	private MediaController controller;

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

        statsIcon = new ImageIcon("images/icons/stats2.png");
		statsButton = new JButton(statsIcon);
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
			}
		});

	}

	private void updateGUI() {
		switch (this.controller.getModel().getState()) {
		case STOP:
		case PAUSE:
			playpauseButton.setIcon(playIcon);
			playpauseButton.setActionCommand("PLAY");
			break;
		case BUFFER:
		case PLAY:
			playpauseButton.setIcon(pauseIcon);
			playpauseButton.setActionCommand("PAUSE");;
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