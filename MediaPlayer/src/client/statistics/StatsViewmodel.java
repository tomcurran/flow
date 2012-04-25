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

public class StatsViewmodel extends Observable implements Observer{
	 
	// Constructor
	public StatsViewmodel (Observable model) {
		model.addObserver(this);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		int[] data = ((int[]) arg1);
		
		data = processData(data);
		
		this.setChanged();
		this.notifyObservers(data);
		
	}
	
	private int[] processData(int[] array){
		int[] reply = array;
		
		//TODO process stuff here
		
		return reply;
	}
}
