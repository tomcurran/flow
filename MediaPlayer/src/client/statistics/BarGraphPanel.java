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

public class BarGraphPanel extends JPanel implements Observer{
	private StatsViewmodel viewModel;
	private final Color BACKGROUND = Color.BLACK;
	private final Color[] LINES = {Color.RED, Color.BLUE, Color.YELLOW};
	
	private int[] lineData;
	
	public BarGraphPanel(Dimension size, Observable model) {
		viewModel = new StatsViewmodel(model);
		viewModel.addObserver(this);
		
		lineData = new int[100];
		Arrays.fill(lineData, 0);
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
		int size = lineData.length;
		
		float widthScale = ((float) this.getWidth())/size;
		float heightScale = ((float) this.getHeight())/100;
		int[] lastCoords = {((int) 0), ((int) (lineData[0]*heightScale))};
		
		for (int i = 1; i < size; i++) {
			int newX = ((int) (lastCoords[0] + widthScale));
			int newY = ((int) (lineData[i]*heightScale));
			g.drawLine(lastCoords[0], lastCoords[1], newX, newY);
			lastCoords[0] = newX;
			lastCoords[1] = newY;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		lineData = (int[]) arg1;
		this.repaint();
		
	}
}
