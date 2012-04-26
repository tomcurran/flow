package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.controller.LibraryController;
import client.controller.MediaController;

import client.statistics.LagGraphPanel;
import client.statistics.LagGraphViewmodel;
import client.statistics.StatisticsModel;
import client.statistics.tests.LineGraphPanel;
import client.statistics.tests.LineGraphViewmodel;

import client.model.MediaPlayer.STATE;
import client.model.Update;


@SuppressWarnings("serial")
public class ClientView extends JFrame implements Observer {

	// TODO Jamie, the commented sections here are for you stats magic.
	private JPanel contentPane;

	private LibraryView libraryView;
	private MediaView mediaView;
	
	// Stats stuff
	private StatisticsModel statsLogger;
	private LineGraphPanel delayGraphPanel;
	private LineGraphPanel jitterGraphPanel;
	private LagGraphPanel lagGraphPanel;

	private PlayerButtonView playerButtons;
	private LibraryButtonView libraryButtons;
	// private StatsButtonView statsButtons;


	private MediaController mediaController;
	private LibraryController libraryController;

	// private StatsController statsController;

	// TODO pass in the stats controller.
	public ClientView(MediaController mediaController,
			LibraryController libraryController, StatisticsModel statsLogger) {
		super("Client");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mediaController = mediaController;
		this.mediaController.getModel().addObserver(this);
		this.libraryController = libraryController;
		this.libraryController.getModel().addObserver(this);
		this.statsLogger = statsLogger;
		initialiseComponents();
		initilaiseListeners();
	}

	private void initialiseComponents() {

		libraryView = new LibraryView(libraryController);
		libraryButtons = new LibraryButtonView(libraryController);

		mediaView = new MediaView(mediaController);
		playerButtons = new PlayerButtonView(mediaController, this);

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
			contentPane.add(mediaView, BorderLayout.CENTER);
			contentPane.add(playerButtons, BorderLayout.SOUTH);

			String media = libraryController.getModel().getSelectedMedia();
			System.out.println("MEDIA WHEN SWITCHED: " + media);
			mediaController.getModel().openMedia(media);
			System.out.println("Switched to mediaplayer");
			super.setContentPane(contentPane);
			super.setMinimumSize(new Dimension(390, 370));

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

	
	public void setStats() {
		// TODO Jamie! Setting stat stuff goes here!
		
		JPanel statsPane = new JPanel(new BorderLayout());
		statsPane.setPreferredSize(new Dimension(105, 55));
		
		//add the statsView and statsButtons to the statsPane.

		delayGraphPanel = new LineGraphPanel(new Dimension(300,100), statsLogger);
		statsPane.add(delayGraphPanel);
		jitterGraphPanel = new LineGraphPanel(new Dimension(300,100), statsLogger);
		statsPane.add(jitterGraphPanel);
		lagGraphPanel = new LagGraphPanel(new Dimension(300, 200), statsLogger);
		
		contentPane.add(statsPane, BorderLayout.EAST);
	}

}