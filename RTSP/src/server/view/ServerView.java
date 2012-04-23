package server.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;

import server.controller.ServerController;
import server.controller.ServerWindowListener;
import server.model.ServerModel;

	/**
	 * @author jwb09119
	 * @date 18/04/2012
	 * 
	 * A basic view for the sever - should show state
	 * and other debug information.
	 */

public class ServerView extends JFrame implements Observer{
	ServerController controller;
	JLabel stateLabel;
	JLabel frameLabel;
	
	// Constructor
	public ServerView (ServerModel model) {
		controller = new ServerController(model);
		this.addWindowListener(new ServerWindowListener(model));

		//showView();
	}

	
	public void showView() {
		
		super.setTitle("Server");
		this.setMinimumSize(new Dimension(200, 200));
		this.setMaximumSize(new Dimension(200, 200));
		this.setResizable(false);
		
		stateLabel = new JLabel("State: ", JLabel.CENTER);
		this.add(stateLabel, BorderLayout.NORTH);
		
		frameLabel = new JLabel("Send frame #", JLabel.CENTER);
		getContentPane().add(frameLabel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	
	public void setStateLabel (String state) {
		if (stateLabel != null ) {
			stateLabel.setText("State: "+ state);
			this.repaint();
		}
	}
	
	
	public void setFrameLabel (String frameNo) {
		if (frameLabel != null) { 
			frameLabel.setText("State: "+ frameNo);
			this.repaint();
		}
	}
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		String state = ((ServerModel)arg0).getState();
		if (state != null) { 
			setStateLabel(state);
		}
		
		if (arg1 != null) {
			String frameNo = ((String)arg1);
			if (frameNo.startsWith("Send")) {
				setFrameLabel(frameNo);
			}
		}
	}

}
