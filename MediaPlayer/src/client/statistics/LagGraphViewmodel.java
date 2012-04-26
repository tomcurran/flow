package client.statistics;

import java.util.Observable;
import java.util.Observer;

	/**
	 * @author jwb09119
	 * @date 25/04/2012
	 * This is a presenter or view-model for the GraphPanel class, 
	 * this class observes the 'raw' data source, pre-processes
	 * any updates into a standardised form.
	 */

public class LagGraphViewmodel extends Observable implements Observer{
	 
	// Constructor
	public LagGraphViewmodel (Observable model) {
		model.addObserver(this);
	}

	
	@Override
	public void update(Observable arg0, Object arg1) {
		int[][] data = ((int[][]) arg1);
		
		for (int i = 0; i < data.length; i++) {
			data[i] = processData(data[i]);
		}
		
		
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
