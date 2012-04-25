package server.rtsp.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import server.rtsp.model.RTSPRequest;
import server.rtsp.model.RTSPRequest.Update;

@SuppressWarnings("serial")
public class ServerView extends JFrame implements Observer {

	private RTSPRequest model;
	private JPanel mainPanel;
	private JLabel stateLabel;
	private JLabel sequenceLabel;
	private JLabel frameLabel;

	public ServerView (RTSPRequest model) {
		super("Server");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.model = model;
		model.addObserver(this);
		initisaliseComponents();
		setTitle();
		setStateLabel();
		setSequenceLabel();
		setFrameLabel();
	}

	private void initisaliseComponents() {
		Border padding = new EmptyBorder(10, 10, 10, 10);
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setPreferredSize(new Dimension(230, 100));
		stateLabel = new JLabel();
		sequenceLabel = new JLabel();
		frameLabel = new JLabel();
		stateLabel.setBorder(padding);
		sequenceLabel.setBorder(padding);
		frameLabel.setBorder(padding);
		mainPanel.add(stateLabel);
		mainPanel.add(sequenceLabel);
		mainPanel.add(frameLabel);
		this.add(mainPanel, BorderLayout.CENTER);
		this.pack();
	}

	public void setStateLabel() {
		stateLabel.setText("Current state: " + model.getState());
	}

	public void setFrameLabel() {
		frameLabel.setText("Sent frame: #" + model.getFrameNumber());
	}

	public void setSequenceLabel() {
		sequenceLabel.setText("Request #" + model.getSquenceNumber());
	}

	public void setTitle() {
		setTitle("Serving client " + model.getSessionId());
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
		case SEQUENCE:
			setSequenceLabel();
			break;
		}
	}

}
