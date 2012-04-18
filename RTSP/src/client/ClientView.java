package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.ClientModel.UpdateReason;


@SuppressWarnings("serial")
public class ClientView extends JFrame implements Observer {

	private JButton setupButton = new JButton("Setup");
	private JButton playButton = new JButton("Play");
	private JButton pauseButton = new JButton("Pause");
	private JButton tearButton = new JButton("Teardown");
	private JPanel mainPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JLabel iconLabel = new JLabel();
	private Toolkit toolkit;
	private ImageIcon icon;

	private ClientController controller;
	private ClientModel model;

	public ClientView(ClientModel model, ClientController controller) {
		super("Client");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.model = model;
        this.model.addObserver(this);
		this.controller = controller;
		initisaliseComponents();
		initialiseActionListeners();
	}

	private void initisaliseComponents() {
		toolkit = Toolkit.getDefaultToolkit();

		// Buttons
		buttonPanel.setLayout(new GridLayout(1, 0));
		buttonPanel.add(setupButton);
		buttonPanel.add(playButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(tearButton);

		// Image display label
		iconLabel.setIcon(null);

		// frame layout
		mainPanel.setLayout(null);
		mainPanel.add(iconLabel);
		mainPanel.add(buttonPanel);
		iconLabel.setBounds(0, 0, 380, 280);
		buttonPanel.setBounds(0, 280, 380, 50);

		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.setSize(new Dimension(390, 370));
		this.setVisible(true);
	}

	private void initialiseActionListeners() {

		setupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.printf("Setup Button pressed!\n");
				controller.setup();
			}
		});

		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.printf("Play Button pressed!\n");
				controller.play();
			}
		});

		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.printf("Pause Button pressed!\n");
				controller.pause();
			}
		});

		tearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.printf("Tear Button pressed!\n");
				controller.tear();
			}
		});

	}

	@Override
	public void update(Observable o, Object arg) {
		UpdateReason reason = (UpdateReason) arg;
		switch (reason) {
		case FRAME:
			Image image = toolkit.createImage(model.getFrame(), 0, model.getFrameLength());
			icon = new ImageIcon(image);
			iconLabel.setIcon(icon);
			break;
		}
	}

}