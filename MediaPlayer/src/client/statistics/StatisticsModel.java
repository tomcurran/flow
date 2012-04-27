package client.statistics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

import javax.swing.Timer;

import server.rtsp.model.RTPpacket;
import sun.security.x509.AVA;

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

public class StatisticsModel extends Observable implements ActionListener{
	private static final int LOG_SIZE = 100; // Number of packets to store in history for stat calculation
	Timer clock;
	
	// Book keeping
	RTPpacket lastPacket;
	int lastPacketArrivalTime;
	int lastPacketSequenceNumber;
	int lastPacketDelay;// last value

	// Accessible stats
	double packetArrivalRate;  //per second
	double packetArrivalDelay; 
	
	int packetJitterAverage; // Running Average (Absolute)
	int packetOutOfSequenceCount;
	Queue<Integer> packetDelays;
	Queue<Integer> packetJitters;
	Queue<Integer> packetArrivalTimes;
	
	//Lag chart data
	int packetsReceived; //
	int packetsPlayed; //
	Queue<Integer> recievedTracker;
	Queue<Integer> playedTracker;
	
	Queue<Integer> packetsRecieved;

	
	// Constructor
	public StatisticsModel(){
		clock = new Timer(200, this); // Updates observers periodically
		packetDelays = new LinkedList<Integer>();
		packetJitters = new LinkedList<Integer>();
		packetArrivalTimes = new LinkedList<Integer>();
		recievedTracker = new LinkedList<Integer>();
		playedTracker= new LinkedList<Integer>();
		
		packetsReceived = 0;
		packetsPlayed = 0; 
		
		lastPacketArrivalTime = 0;
		packetArrivalRate = 0;

		
		packetDelays.add(0);
		while(packetDelays.size() < LOG_SIZE) {
			packetDelays.add(0);
		}
		
		packetJitters.add(0);
		while(packetJitters.size() < LOG_SIZE) {
			packetJitters.add(0);
		}
		
		recievedTracker.add(0);
		while(recievedTracker.size() < LOG_SIZE) {
			recievedTracker.add(0);
		}
		
		playedTracker.add(0);
		while(playedTracker.size() < LOG_SIZE) {
			playedTracker.add(0);
		}
		
		clock.start();
	}

	
	/*
	 * Log a packet received by the client
	 */
	public void logPacketReceived(RTPpacket packet, int arrivalTime) {
		packetsReceived++;
		if (packetArrivalTimes.size() >= LOG_SIZE) {
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
		
	}
	
	
	public void logFrameExitFromBuffer() {
		packetsPlayed++;
	}
	
	
	private void logPacketDelay(int arrivalTime, int timeStamp) {
		int newDelay = arrivalTime - timeStamp;
		
		while (packetDelays.size() >= LOG_SIZE) {
			packetDelays.poll();
		}
		packetDelays.add(newDelay);
		
		int newJitter = Math.abs((newDelay-lastPacketDelay));
		lastPacketDelay = newDelay;
		
		while (packetJitters.size() >= LOG_SIZE) {
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
		
		if(numberOfPackets <= 1){
			return;
		}
		int totalTime = lastPacketArrivalTime - packetArrivalTimes.peek();
		packetArrivalRate = ((double)numberOfPackets / (((double)totalTime) / 1000)); // milliseconds
	}
	
	
	private void recalculatePacketArrivalDelays() {
		int total = 0;
		int size = packetDelays.size();
		for(int i : packetDelays) {
			total += i;
		}
		packetArrivalDelay = total/size;
	}
	
	
	private void recalculatePacketJitters(){
		int total = 0;
		int size = packetJitters.size();
		for(int i : packetJitters) {
			total += i;
		}
		packetJitterAverage = total/size;
	}

	
	protected List<Integer> getPacketsReceived(){
		return (List<Integer>) recievedTracker;
	}
	
	
	protected List<Integer> getPacketsPlayed(){
		return (List<Integer>) playedTracker;
	}
	
	
	protected List<Integer> getJitterData(){
		return (List<Integer>) packetJitters;
	}
	
	
	protected List<Integer> getDelayData(){
		return (List<Integer>) packetDelays;
	}
	
	public double getPacketArrivalRate() {
		return packetArrivalRate;
	}
	public double getAverageArrivalDelay() {
		return packetArrivalDelay;
	}
	public int getOutOfOrderCount() {
		return packetOutOfSequenceCount;
	}
	public double getAveragePacketJitter(){
		return packetJitterAverage;
	}
	

	private void updateTrackers(){
		
		while (recievedTracker.size() >= LOG_SIZE) {
			recievedTracker.poll();
		}
		recievedTracker.add(packetsReceived);
		
		while (playedTracker.size() >= LOG_SIZE) {
			playedTracker.poll();
		}
		playedTracker.add(packetsPlayed);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		updateTrackers();
		this.setChanged();
		this.notifyObservers();
	}
	
}
