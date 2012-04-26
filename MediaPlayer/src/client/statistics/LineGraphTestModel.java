package client.statistics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import java.util.Random;

import javax.swing.Timer;

class LineGraphTestModel extends Observable implements ActionListener{
	Random random;
	Queue<Integer> rawData;
	Timer clock;
	ActionListener listener;
	
	public LineGraphTestModel() {
		rawData = new LinkedList<Integer>();
		random = new Random();
		clock = new Timer(100, this);
		
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
		rawData.poll();
		rawData.add(random.nextInt(500));
		this.setChanged();
		this.notifyObservers(getData());
	}
	
}