package client.statistics;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MetricsPanel extends JPanel implements Observer {
	
	private JLabel packetArrivalRate;
	private JLabel arrivalDelayRate;
	private JLabel outofOrderCount;
	private JLabel jitterAvg;
	
	private Integer packArrRate = 0;
	private Integer arrDelRate = 0;
	private Integer outOrderCount = 0;
	private Integer jittAvg = 0;
	
	public MetricsPanel(){
		packetArrivalRate = new JLabel("Packet Arrival Rate: " + packArrRate);
		arrivalDelayRate = new JLabel("Arrival Delay Rate: " + arrDelRate);
		outofOrderCount = new JLabel("Out of order count: " + outOrderCount);
		jitterAvg = new JLabel("Jitter Average: " + jittAvg);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Update te int values, the rest should work just fine I think

	}

}
