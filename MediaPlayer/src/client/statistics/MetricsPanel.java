package client.statistics;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MetricsPanel extends JPanel implements Observer {
	StatisticsModel model;
	
	private JLabel packetArrivalRate;
	private JLabel arrivalDelayRate;
	private JLabel outofOrderCount;
	private JLabel jitterAvg;
	
	private double packArrRate = 0;
	private double arrDelRate = 0;
	private int outOrderCount = 0;
	private double jittAvg = 0;
	
	public MetricsPanel(Observable datasource){
		datasource.addObserver(this);
		this.model = (StatisticsModel) datasource;
		
		packetArrivalRate = new JLabel("Packet Arrival Rate: " + packArrRate);
		arrivalDelayRate = new JLabel("Arrival Delay Rate: " + arrDelRate);
		outofOrderCount = new JLabel("Out of order count: " + outOrderCount);
		jitterAvg = new JLabel("Jitter Average: " + jittAvg);
		this.add(packetArrivalRate);
		this.add(arrivalDelayRate);
		this.add(outofOrderCount);
		this.add(jitterAvg);
		this.setVisible(true);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		packArrRate = model.getPacketArrivalRate();
		arrDelRate = model.getAverageArrivalDelay();
		outOrderCount = model.getOutOfOrderCount();
		jittAvg = model.getAveragePacketJitter();
		
		packetArrivalRate.setText("Packet Arrival Rate: " + String.format("%.5g%n", packArrRate));
		arrivalDelayRate.setText("Arrival Delay Rate: " + String.format("%.2g%n", arrDelRate));
		outofOrderCount.setText("Out of order count: " + outOrderCount);
		jitterAvg.setText("Jitter Average: " + String.format("%.2g%n", arrDelRate));
	
		repaint();
	}

}
