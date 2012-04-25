package client.statistics;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import java.util.Random;
import javax.swing.Timer;

public class GraphTest implements ActionListener{

	public static void main (String args[]) {

		
		DummyModel model = new DummyModel();
		GraphPanel graph = new GraphPanel(new Dimension(100, 50), model);
		
		
		model.run();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

class DummyModel extends Observable implements ActionListener{
	Random random;
	Queue<Integer> rawData;
	Timer clock;
	ActionListener listener;
	
	public DummyModel() {
		rawData = new LinkedList<Integer>();
		random = new Random();
		clock = new Timer(1000, this);

		for(int i = 0; i < 100; i++) {
			rawData.add( random.nextInt(500));
		}
		
	}
	
	public void run(){
		clock.start();
	}
	
	
	protected int[] getData(){
		
		int[] reply = new int[rawData.size()];
		int count = 0;
		
		for (Integer i : rawData) {
			reply[count] = i;
			count++;
		}
		
		return reply;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.setChanged();
		this.notifyObservers(getData());
	}
	
}
