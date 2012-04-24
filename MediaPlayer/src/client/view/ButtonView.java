package client.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.controller.MediaController;
import client.model.MediaPlayer.Update;

@SuppressWarnings("serial")
public class ButtonView extends JPanel implements Observer {
	
	private JButton playpause = new JButton();

	private MediaController controller;

	public ButtonView(MediaController controller) {
		this.controller = controller;
		this.controller.getModel().addObserver(this);
		initialiseComponents();
		updatePlaypauseText();
		initialiseListeners();
	}

	private void initialiseComponents() {
		this.setLayout(new GridLayout(1, 0));
		this.add(playpause);
	}

	private void initialiseListeners() {

		playpause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.playpause(playpause.getText());
			}
		});

	}

	private void updatePlaypauseText() {
		switch (this.controller.getModel().getState()) {
		case STOP:
		case PAUSE:
			playpause.setText("Play");
			break;
		case BUFFER:
		case PLAY:
			playpause.setText("Pause");
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((Update) arg) {
		case STATE:
			updatePlaypauseText();
			break;
		default:
			break;
		}
	}

}