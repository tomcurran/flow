package client.statistics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import java.awt.Color;

	/**
	 * @author jwb09119
	 * @date 25/04/2012
	 * 
	 *Represents graphing data, obtained by observing an
	 * updating data source (model)
	 *
	 */

public class LagGraphPanel extends JPanel implements Observer{
	private LagGraphViewmodel viewModel;
	private final Color BACKGROUND = Color.BLACK;
	private final Color[] LINES = {Color.RED, Color.ORANGE, Color.GREEN};
	
	private int[] sentData;
	private int[] recievedData;
	private int[] playedData;
	
	public LagGraphPanel(Dimension size, Observable model) {
		viewModel = new LagGraphViewmodel(model);
		viewModel.addObserver(this);
		
		sentData = new int[100];
		recievedData = new int[100];
		playedData = new int[100];
		
		Arrays.fill(sentData, 0);
		Arrays.fill(recievedData, 0);
		Arrays.fill(playedData, 0);
		
		this.setSize(size);
	}
	
	
	private void initialiseSwing(){
		this.setMinimumSize(this.getSize());
		this.setMaximumSize(this.getSize());
		this.setDoubleBuffered(true);
		this.setVisible(true);
	}
	
	
	@Override
	public void paint(Graphics g){
		// Fill background
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// Draw graph line
		g.setColor(LINES[0]);
		int size = sentData.length;
		
		float widthScale = ((float) this.getWidth())/size;
		float heightScale = ((float) this.getHeight())/100;
		
		int[] lastSent = {((int) 0), ((int) (sentData[0]*heightScale))};
		int[] lastRecieved = {((int) 0), ((int) (recievedData[0]*heightScale))};
		int[] lastPlayed = {((int) 0), ((int) (playedData[0]*heightScale))};
		
		for (int i = 1; i < size; i++) {
			int newX = ((int) (lastSent[0] + widthScale));
			int newY = ((int) (lastSent[i]*heightScale));
			g.drawLine(lastSent[0], lastSent[1], newX, newY);
			lastSent[0] = newX;
			lastSent[1] = newY;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		int[][] data = (int[][]) arg1;
		
		sentData = data[0];
		recievedData = data[1];
		playedData = data[2];
		
		this.repaint();
		
	}
}
