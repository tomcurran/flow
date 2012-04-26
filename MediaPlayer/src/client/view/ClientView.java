package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.controller.LibraryController;
import client.controller.MediaController;
import client.model.Library.Update;
import client.statistics.InboundLoggingController;
import client.statistics.LagGraphPanel;
import client.statistics.LagGraphViewmodel;
import client.statistics.LineGraphPanel;
import client.statistics.StatisticsModel;
import client.statistics.tests.LineGraphViewmodel;

@SuppressWarnings("serial")
public class ClientView extends JFrame implements Observer{

//TODO Jamie, the commented sections here are for you stats magic. 
	private JPanel contentPane;
	
	private LibraryView libraryView;
	private MediaView mediaView;
	
	// Stats stuff
	private LineGraphPanel delayGraphPanel;
	private LineGraphViewmodel delayGraphViewmodel;
	
	private LineGraphPanel jitterGraphPanel;
	private LineGraphViewmodel jitterGraphViewmodel;
	
	private LagGraphPanel lagGraphPanel;
	private LagGraphViewmodel lagGraphViewmodel;
	
	
	private PlayerButtonView playerButtons;
	private LibraryButtonView libraryButtons;
	
	private MediaController mediaController;
	private LibraryController libraryController;

	//TODO pass in the stats controller. 
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
		Update command = (Update) arg;
		System.out.println(" Command: " + command.toString());
		
		if(command == Update.SELECTED){
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
		}else if (command == Update.STATSON){
			//declare a panel for the graphs and the control sliders to be added.
			JPanel statsPane = new JPanel(new BorderLayout());
			statsPane.setPreferredSize(new Dimension(105, 55));
			
			//add the statsView and statsButtons to the statsPane.
			StatisticsModel statsModel = InboundLoggingController.getInstance().getStatisticsModel();
			delayGraphViewmodel = new LineGraphViewmodel(statsModel);
			delayGraphPanel = new LineGraphPanel(new Dimension(300,100), delayGraphViewmodel);
			statsPane.add(delayGraphPanel);
			
			jitterGraphViewmodel = new LineGraphViewmodel(statsModel);
			jitterGraphPanel = new LineGraphPanel(new Dimension(300,100), jitterGraphViewmodel);
			statsPane.add(jitterGraphPanel);
			
			lagGraphViewmodel = new LagGraphViewmodel(statsModel);
			lagGraphPanel = new LagGraphPanel(new Dimension(300, 200), lagGraphViewmodel);
			
			//and the statsPane to the BorderLayou.EAST of the contentPane.
			contentPane.add(statsPane, BorderLayout.EAST);
		} else{
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