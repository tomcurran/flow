package client.statistics;

import javax.xml.ws.soap.Addressing;

import server.rtsp.model.RTPpacket;

	/**
	 * @author jwb09119
	 * @date 24/12/2012
	 * Handled inbound logging events and pre-processes them before
	 * handing them back to the StatisticsModel.  This makes heavy
	 * use of the Observer-Observable
	 * Refrerence: http://en.wikibooks.org/wiki/Java_Programming/Design_Patterns
	 */

public class InboundLoggingController{
	
	public static InboundLoggingController object; // Not initialised until called for
	private static String lockObj = "Lock";  // Used for locking
	
	private StatisticsModel model;;
	
	// Constructor
	private InboundLoggingController(){
		// Cannot be called directly
	}
	
	public static InboundLoggingController getInstance() {
		if(object != null) {
			return object;
		} else {
			// start a synchronised block, invocations may not interleave
			synchronized(lockObj) {
				if (object == null) {
					object = new InboundLoggingController(); // Only assigned if not already initialised
				}
			} // End of synchronised block
			return object;
		}
	}
	
	
	public synchronized void logPacketReceipt(RTPpacket packet, int arrivalTime){
		model.logPacketReceived(packet, arrivalTime);
	}
	
	public void logFramePlayed(){
		model.logFrameExitFromBuffer();
	}
	
	
	public void setModel(StatisticsModel model) {
		this.model = model;
	}
	
	public StatisticsModel getStatisticsModel(){
		return model;
	}
	
	
}
