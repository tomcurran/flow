package client.statistics.tests;

import java.awt.Dimension;

import client.statistics.LagGraphPanel;


public class LagGraphTest{

	public static void main (String args[]) {

				LagGraphTestModel modelB = new LagGraphTestModel();
				LagGraphPanel graphB = new LagGraphPanel(new Dimension(300, 100), modelB);
				GraphTestClient clientB = new GraphTestClient(graphB);
				
				modelB.run();
	}
}

