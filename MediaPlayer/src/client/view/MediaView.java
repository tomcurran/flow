package client.view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.controller.MediaController;
import client.model.MediaPlayer;
import client.model.Update;

@SuppressWarnings("serial")
public class MediaView extends JPanel implements Observer {

	private Toolkit toolkit;
	private JLabel statusLabel;
	private JLabel iconLabel;

	private MediaController controller;

	public MediaView(MediaController controller) {
		this.controller = controller;
		this.controller.getModel().addObserver(this);
		this.toolkit = Toolkit.getDefaultToolkit();
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
		switch ((Update) arg) {
		case FRAME:
			System.out.println("Updating frame");
			updateFrame();
			break;
		case STATE:
			updateState();
			break;
		default:
			break;
		}
	}

	private void updateFrame() {
		System.out.println("Setting Frame");
		MediaPlayer model = this.controller.getModel();
		iconLabel.setIcon(new ImageIcon(toolkit.createImage(model.getFrame(), 0, model.getFrameLength())));
	}

	private void updateState() {
		MediaPlayer model = this.controller.getModel();
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
	}

}