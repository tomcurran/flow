package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.controller.LibraryController;
import client.controller.MediaController;

@SuppressWarnings("serial")
public class ClientView extends JFrame implements Observer{

	private JPanel contentPane;
	
	private LibraryView libraryView;
	private MediaView mediaView;
	private PlayerButtonView playerButtons;
	private LibraryButtonView libraryButtons;

	private MediaController mediaController;
	private LibraryController libraryController;

	public ClientView(MediaController mediaController, LibraryController libraryController) {
		super("Client");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mediaController = mediaController;
		this.libraryController = libraryController;
		this.libraryController.getModel().addObserver(this);
		initialiseComponents();
		initilaiseListeners();
	}

	private void initialiseComponents() {

		libraryView = new LibraryView(libraryController);
		libraryButtons = new LibraryButtonView(libraryController);
		
		mediaView = new MediaView(mediaController);
		playerButtons = new PlayerButtonView(mediaController);
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.setPreferredSize(new Dimension(380, 280));
		contentPane.add(libraryView, BorderLayout.CENTER);
		contentPane.add(libraryButtons, BorderLayout.SOUTH);

		super.setContentPane(contentPane);
		super.setMinimumSize(new Dimension(390, 370));

		super.pack();
		super.requestFocus();
	}

	private void initilaiseListeners() {
	}

	@Override
	public void update(Observable o, Object arg) {
		String command = (String) arg;
		if(command.equals("PLAYMODE")){
			contentPane.removeAll();
			contentPane.add(mediaView, BorderLayout.CENTER);
			contentPane.add(playerButtons, BorderLayout.SOUTH);
			
			super.setContentPane(contentPane);
			super.setMinimumSize(new Dimension(390, 370));

			super.pack();
			super.requestFocus();
		}else{
			contentPane.removeAll();
			contentPane.add(libraryView, BorderLayout.CENTER);
			contentPane.add(libraryButtons, BorderLayout.SOUTH);

			super.setContentPane(contentPane);
			super.setMinimumSize(new Dimension(390, 370));

			super.pack();
			super.requestFocus();
		}
	}

}