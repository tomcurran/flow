package client.statistics;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;

public class GraphTestClient{
	JFrame frame;
	
	public GraphTestClient(Component c) {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(105,55));
		frame.add(c);
		frame.setVisible(true);
	}
	
}
