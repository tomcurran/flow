package server.rtsp.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;

import server.rtsp.model.RTSPRequest;
import server.rtsp.model.RTSPRequest.Update;

@SuppressWarnings("serial")
public class ServerView extends JFrame implements Observer {

	private RTSPRequest model;
	private JLabel stateLabel;
	private JLabel frameLabel;

	public ServerView (RTSPRequest model) {
		super("Server");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.model = model;
		model.addObserver(this);
		initisaliseComponents();
		setStateLabel();
		setFrameLabel();
	}

	private void initisaliseComponents() {
		this.setMinimumSize(new Dimension(200, 200));
		this.setMaximumSize(new Dimension(200, 200));
		this.setResizable(false);
		stateLabel = new JLabel();
		this.add(stateLabel, BorderLayout.NORTH);
		frameLabel = new JLabel();
		getContentPane().add(frameLabel, BorderLayout.CENTER);
	}

	public void setStateLabel() {
		stateLabel.setText("State: " + model.getState());
	}

	public void setFrameLabel() {
		frameLabel.setText("Frame: #" + model.getFrameNumber());
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((Update) arg) {
		case STATE:
			setStateLabel();
			break;
		case FRAME:
			setFrameLabel();
			break;
		}
	}

}
