package client.statistics;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

import server.rtsp.model.RTPpacket;

	/**
	 * @author jwb09119
	 * @date 24/04/2012
	 * Represents the statisics gathered from various places in the
	 * client, and other data extrapolated from this.  this object will
	 * be observed by elements of the GUI for the purposes of gathering
	 * graph data, etc
	 * 
	 * // TODO - the timestamp field in RTPpacket is just frameNo*frameLength
	 *           and does not represent when the packet was sent, this is of limited
	 *           use when calculating delay.  Can we change or add an actual departure time?
	 */

public class StatisticsModel extends Observable{
	private static final int LOG_SIZE = 10; // Number of packets to store in history for stat calculation
	InboundLoggingController logger;
	
	// Book keeping
	RTPpacket lastPacket;
	int lastPacketArrivalTime;
	int lastPacketSequenceNumber;
	int lastPacketDelay;

	// Accessible stats
	int packetArrivalRate;  //per second
	int packetArrivalDelay; // last value
	int packetArrivalDelayAverage; // TODO - calculate this
	
	int packetJitterAverage; // Running Average (Absolute)
	int packetOutOfSequenceCount;
	Queue<Integer> packetDelays;
	Queue<Integer> packetJitters;
	Queue<Integer> packetArrivalTimes;

	
	// Constructor
	public StatisticsModel() {
		packetDelays = new LinkedList<Integer>();
		packetJitters = new LinkedList<Integer>();
		packetArrivalTimes = new LinkedList<Integer>();
		
		logger = InboundLoggingController.getInstance();
		logger.setModel(this);
		lastPacketArrivalTime = 0;
		packetDelays.add(0);
		packetJitters.add(0);
	}

	
	/*
	 * Log a packet received by the client
	 */
	protected void logPacketReceived(RTPpacket packet, int arrivalTime) {
		if (packetArrivalTimes.size() > LOG_SIZE) {
			packetArrivalTimes.poll();
		}
		packetArrivalTimes.add(arrivalTime);
		
		logPacketDelay(arrivalTime, packet.getHeaderSsrc());
		logPacketSequence(arrivalTime, packet.getSequenceNumber());
		recalculateArrivalRate(arrivalTime);
			
		//finally
		lastPacket = packet;
		lastPacketArrivalTime = arrivalTime;	
		lastPacketSequenceNumber = packet.getSequenceNumber();
		
		this.setChanged();
		this.notifyObservers();
	}
	
	
	private void logPacketDelay(int arrivalTime, int timeStamp) {
		int newDelay = arrivalTime - timeStamp;
		
		if (packetDelays.size() > LOG_SIZE) {
			packetDelays.poll();
		}
		
		int newJitter = Math.abs((newDelay-lastPacketDelay));
		lastPacketDelay = newDelay;
		
		if (packetJitters.size() > LOG_SIZE) {
			packetJitters.poll();
		}
		packetJitters.add(newJitter);
		
		recalculatePacketArrivalDelays();
		recalculatePacketJitters();
	}


	private void logPacketSequence(int arrivalTime, int sequenceNumber) {
		if((lastPacketSequenceNumber+1) != sequenceNumber){
			packetOutOfSequenceCount++;
		}
	}
	
	
	private void recalculateArrivalRate(int arrivalTime) {
		int numberOfPackets=packetArrivalTimes.size();
		float totalTime = lastPacketArrivalTime - packetArrivalTimes.peek();
		float packetsPerSecond = numberOfPackets / (totalTime / 1000); // milliseconds
	}
	
	
	private void recalculatePacketArrivalDelays() {
		int total = 0;
		int size = packetDelays.size();
		for(Integer i : packetDelays) {
			total += i;
		}
		packetArrivalDelay = total/size;
	}
	
	
	private void recalculatePacketJitters(){
		int total = 0;
		int size = packetJitters.size();
		for(Integer i : packetJitters) {
			total += i;
		}
		packetJitterAverage = total/size;
	}
	
}
