package client.view;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import client.controller.MediaController;
import client.model.MediaPlayer;
import client.model.MediaPlayer.Update;

@SuppressWarnings("serial")
public class MediaView extends JPanel implements Observer {

	private JLabel statusLabel;
	private JLabel iconLabel;

	private MediaController controller;

	public MediaView(MediaController controller) {
		this.controller = controller;
		this.controller.getModel().addObserver(this);
		initialiseComponents();
		initialiseListeners();
	}

	private void initialiseComponents() {
		statusLabel = new JLabel();
		iconLabel = new JLabel();
		iconLabel.setIcon(null);
		this.add(statusLabel);
		this.add(iconLabel);
		this.setPreferredSize(new Dimension(380, 280));
	}

	private void initialiseListeners() {
	}

	@Override
	public void update(Observable o, Object arg) {
		MediaPlayer model = this.controller.getModel();
		switch ((Update) arg) {
		case FRAME:
			iconLabel.setIcon(model.getFrame());
			break;
		case STATE:
			switch (model.getState()) {
			case STOP:
				statusLabel.setText("Stopped");
				break;
			case BUFFER:
				statusLabel.setText("Buffering...");
				break;
			case PLAY:
				statusLabel.setText("Playing");
				break;
			case PAUSE:
				statusLabel.setText("Paused");
				break;
			}
			break;
		default:
			break;
		}
	}

}