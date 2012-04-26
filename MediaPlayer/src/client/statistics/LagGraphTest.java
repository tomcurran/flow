package client.statistics;

import java.awt.Dimension;


public class LagGraphTest{

	public static void main (String args[]) {

				LagGraphTestModel modelB = new LagGraphTestModel();
				LagGraphPanel graphB = new LagGraphPanel(new Dimension(300, 100), modelB);
				GraphTestClient clientB = new GraphTestClient(graphB);
				
				modelB.run();
	}
}

