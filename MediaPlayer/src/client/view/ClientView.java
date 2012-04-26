package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.controller.LibraryController;
import client.controller.MediaController;

import client.model.Update;
import client.statistics.*;

import client.model.MediaPlayer.STATE;
import client.model.Update;


@SuppressWarnings("serial")
public class ClientView extends JFrame implements Observer {

	// TODO Jamie, the commented sections here are for you stats magic.
	private JPanel contentPane;
	private JPanel playerPane;
	private LibraryView libraryView;
	private MediaView mediaView;
	
	// Stats stuff
	private LineGraphPanel delayGraphPanel;
	private LineGraphViewmodel delayGraphViewmodel;
	
	private LineGraphPanel jitterGraphPanel;
	private LineGraphViewmodel jitterGraphViewmodel;
	
	private LagGraphPanel lagGraphPanel;
	private LagGraphViewmodel lagGraphViewmodel;
	
	// private StatsView statsView;

	private PlayerButtonView playerButtons;
	private LibraryButtonView libraryButtons;
	// private StatsButtonView statsButtons;


	private MediaController mediaController;
	private LibraryController libraryController;

	private boolean statsOn = false;
	// private StatsController statsController;

	// TODO pass in the stats controller.
	public ClientView(MediaController mediaController,
			LibraryController libraryController) {
		super("Client");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mediaController = mediaController;
		this.mediaController.getModel().addObserver(this);
		this.libraryController = libraryController;
		this.libraryController.getModel().addObserver(this);
		initialiseComponents();
		initilaiseListeners();
	}

	private void initialiseComponents() {

		libraryView = new LibraryView(libraryController);
		libraryButtons = new LibraryButtonView(libraryController);

		mediaView = new MediaView(mediaController);
		playerButtons = new PlayerButtonView(mediaController, this);

		playerPane = new JPanel(new BorderLayout());
		
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.setPreferredSize(new Dimension(380, 280));
		contentPane.add(libraryView, BorderLayout.CENTER);
		contentPane.add(libraryButtons, BorderLayout.NORTH);

		super.setContentPane(contentPane);
		super.setMinimumSize(new Dimension(390, 370));

		super.pack();
		super.requestFocus();
	}

	private void initilaiseListeners() {
	}

	@Override
	public void update(Observable o, Object arg) {
		Update command = (client.model.Update) arg;
		System.out.println(" Command: " + command.toString());

		if (command == Update.SELECTED) {
			contentPane.removeAll();
			playerPane.removeAll();
			mediaView = new MediaView(mediaController);
			mediaView.setPreferredSize(new Dimension(384, 288));
			mediaView.setBackground(new Color(7, 54, 56));
			playerPane.add(mediaView, BorderLayout.CENTER);
			playerPane.add(playerButtons, BorderLayout.SOUTH);
			contentPane.add(playerPane, BorderLayout.CENTER);

			String media = libraryController.getModel().getSelectedMedia();
			System.out.println("MEDIA WHEN SWITCHED: " + media);
			mediaController.getModel().openMedia(media);
			System.out.println("Switched to mediaplayer");
			super.setContentPane(contentPane);
			super.setMinimumSize(new Dimension(300, 400));

			super.pack();
			super.requestFocus();
			super.repaint();
		} else if (command == Update.STATSON) {
			// declare a panel for the graphs and the control sliders to be
			// added.
			
			
			
		} else if (command == Update.STATE) {
			if (mediaController.getModel().getState() == STATE.RETURN) {

				contentPane.removeAll();
				contentPane.add(libraryView, BorderLayout.CENTER);
				contentPane.add(libraryButtons, BorderLayout.NORTH);

				super.setContentPane(contentPane);
				super.setMinimumSize(new Dimension(390, 370));

				super.pack();
				super.requestFocus();
				super.repaint();
			}
		}
	}

	JPanel statsPane = new JPanel(new BorderLayout());
	public void setStats() {
		// TODO Jamie! Setting stat stuff goes here!
		if(statsOn == false){
			
			statsPane.setPreferredSize(new Dimension(300, 400));
			
			//add the statsView and statsButtons to the statsPane.
			StatisticsModel statsModel = InboundLoggingController.getInstance().getStatisticsModel();
			delayGraphViewmodel = new LineGraphViewmodel(statsModel);
			delayGraphPanel = new LineGraphPanel(new Dimension(300,100), delayGraphViewmodel);
			statsPane.add(delayGraphPanel, BorderLayout.CENTER);
			
			jitterGraphViewmodel = new LineGraphViewmodel(statsModel);
			jitterGraphPanel = new LineGraphPanel(new Dimension(300,100), jitterGraphViewmodel);
			statsPane.add(jitterGraphPanel, BorderLayout.SOUTH);
			
			lagGraphViewmodel = new LagGraphViewmodel(statsModel);
			lagGraphPanel = new LagGraphPanel(new Dimension(300, 200), lagGraphViewmodel);
			
			contentPane.add(statsPane, BorderLayout.EAST);
			super.setMinimumSize(new Dimension(700, 370));
			statsOn = true;
		} else {
			contentPane.remove(statsPane);
			super.setMinimumSize(new Dimension(390, 370));
			statsOn = false;
		}
		super.setContentPane(contentPane);
		

		super.pack();
		super.requestFocus();
		super.repaint();
		
		

	}

}