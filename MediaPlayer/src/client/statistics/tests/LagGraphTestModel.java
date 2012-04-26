package client.statistics.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import java.util.Random;

import javax.swing.Timer;

class LagGraphTestModel extends Observable implements ActionListener{
	private Random random;
	private Timer clock;
	
	private Queue<Integer> packetsSent;
	private Queue<Integer> packetsRecieved;
	private Queue<Integer> packetsPlayed;
	
	
	public LagGraphTestModel() {
		packetsSent = new LinkedList<Integer>();
		packetsRecieved = new LinkedList<Integer>();
		packetsPlayed = new LinkedList<Integer>();
		
		for(int i = 0; i < 100; i++) {
			packetsSent.add(0);
			packetsRecieved.add(0);
			packetsPlayed.add(0);
		}
		
		random = new Random();
		clock = new Timer(500, this);
		
		int sent = 0;
		int inTransit = 0;
		int recieved = 0;
		int buffered = 0;
		int alreadyPlayed = 0;
		for(int i = 0; i < 100; i++) {
			sent++;
			
			while(packetsSent.size() > 99) {
				packetsSent.poll();
			}
			
			packetsSent.add(sent);
			inTransit++;
			
			if(inTransit > 0 && random.nextFloat() > 0.2) {
				inTransit--;
				recieved++;
			}
			
			while(packetsRecieved.size() > 99) {
				packetsRecieved.poll();
			}
			packetsRecieved.add(recieved);

			buffered = recieved - alreadyPlayed;
			if(buffered > 10) {
				alreadyPlayed++;
			}
			while(packetsPlayed.size() > 99) {
				packetsPlayed.poll();
			}
			packetsPlayed.add(alreadyPlayed);
		}
		
	}
	
	
	public void run(){
		clock.start();

	}
	
	
	protected int[][] getData(){
		
		int[][] reply = new int[3][100];
		int count = 99;
		
		for (Integer i : packetsSent) {
			reply[0][count] = i;
			count--;
		}
		
		count = 99;
		
		for (Integer i : packetsRecieved) {
			reply[1][count] = i;
			count--;
		}
		
		count = 99;
		
		for (Integer i : packetsPlayed) {
			reply[2][count] = i;
			count--;
		}
		
		return reply;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {

		this.setChanged();
		this.notifyObservers(getData());
	}
	
}