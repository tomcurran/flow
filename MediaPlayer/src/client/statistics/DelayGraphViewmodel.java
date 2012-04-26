package client.statistics;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

	/**
	 * @author jwb09119
	 * @date 25/04/2012
	 * This is a presenter or view-model for the GraphPanel class, 
	 * this class observes the 'raw' data source, pre-processes
	 * any updates into a standardised form.
	 */

public class DelayGraphViewmodel extends Observable implements Observer{
	StatisticsModel model;
	int[] data; 
	
	
	// Constructor
	public DelayGraphViewmodel (Observable model) {
		model.addObserver(this);
		this.model = (StatisticsModel) model;
	}

	
	@Override
	public void update(Observable arg0, Object arg1) {
		List<Integer> rawData = model.getDelayData();
		
		int size = rawData.size();
		data = new int[size];
		
		if (size > 0) {
			int count = rawData.size();
			for (int i : rawData) {
				data[size-count] = i;
			}
			
			data = processData(data);
			
			this.setChanged();
			this.notifyObservers(data);
		}	
	}
	
	
	private int[] processData(int[] array){
		int[] reply = array;
		
		// get top - round up from highest value
		int max = getMax(reply);
		
		int size = reply.length;
		for (int i = 0; i < size; i++) {
			int source = array[i];
			reply[i] = ((source/max) * 100);
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

		return reply == 0 ? 100 : reply;
	}
}
