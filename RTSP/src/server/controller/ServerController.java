package server.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import server.model.ServerModel;

	/**
	 * @author jwb09119
	 * @date 18/04/2012
	 * 
	 * Mediates between ServerModel and ServerView
	 * This controller is for ServerView.java
	 */

public class ServerController implements ActionListener{

	ServerModel model;
	
	// Constructor
	public ServerController (ServerModel model) {
		
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
