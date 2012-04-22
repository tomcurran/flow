package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.controller.MediaController;

@SuppressWarnings("serial")
public class ClientView extends JFrame {

	private JPanel contentPane;
	private MediaView mediaView;
	private ButtonView buttonView;

	private MediaController controller;

	public ClientView(MediaController controller) {
		super("Client");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.controller = controller;
		initialiseComponents();
		initilaiseListeners();
	}

	private void initialiseComponents() {

		mediaView = new MediaView(controller);
		buttonView = new ButtonView(controller);

		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.setPreferredSize(new Dimension(380, 280));
		contentPane.add(mediaView, BorderLayout.CENTER);
		contentPane.add(buttonView, BorderLayout.SOUTH);

		super.setContentPane(contentPane);
		super.setMinimumSize(new Dimension(390, 370));

		super.pack();
		super.requestFocus();
	}

	private void initilaiseListeners() {
	}

}