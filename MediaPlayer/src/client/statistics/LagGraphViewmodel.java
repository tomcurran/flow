package client.statistics;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

	/**
	 * @author jwb09119
	 * @date 25/04/2012
	 * This is a presenter or view-model for the GraphPanel class, 
	 * this class observes the 'raw' data source, pre-processes
	 * any updates into a standardised form.
	 */

public class LagGraphViewmodel extends Observable implements Observer{ 
	StatisticsModel model;
	
	Queue<Integer> packetsRecieved;
	Queue<Integer> packetsPlayed;
	
	// Constructor
	public LagGraphViewmodel (Observable model) {
		model.addObserver(this);
		this.model = (StatisticsModel) model;
		
		packetsRecieved = new LinkedList<Integer>();
		packetsPlayed = new LinkedList<Integer>();
	}

	
	@Override
	public void update(Observable arg0, Object arg1) {
		packetsRecieved.add(model.getPacketsReceived());
		packetsPlayed.add(model.getPacketsPlayed());
		
		while (packetsRecieved.size() > 100) {
			packetsRecieved.poll();
		}
		while (packetsPlayed.size() > 100) {
			packetsPlayed.poll();
		}
		
		int[][]data = new int[2][100];
		
		int count = 99;
		for (Integer i : packetsRecieved) {
			data[0][99-count] = i;
		}
		
		count = 99;
		for (Integer i : packetsPlayed) {
			data[1][99-count] = i;
		}
		
		data[0] = processData(data[0]);
		data[1] = processData(data[1]);
		
		this.setChanged();
		this.notifyObservers(data);
	}
	
	
	private int[] processData(int[] array){
		int[] reply = array;
		
		// get top - round up from highest value
		int max = getMax(reply);
		
		int size = reply.length;
		for (int i = 0; i < size; i++) {
			float source = array[i];
			reply[i] = ((int)((source/max) * 100));
		}
		
		return reply;
	}
	
	
	private int getMax (int[] array) {
		
		int reply = 0;
		int size = array.length;
		
		for (int i = 0; i < size; i++) {
			int candidate = array[i];
			if(candidate > reply) {
				reply = candidate;
			}
		}

		return reply;
	}
}
