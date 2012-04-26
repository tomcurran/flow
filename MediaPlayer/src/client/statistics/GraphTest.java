package client.statistics;

import java.awt.Dimension;


public class GraphTest{

	public static void main (String args[]) {
		

		    	
				LineGraphTestModel modelA = new LineGraphTestModel();
				LineGraphPanel graphA = new LineGraphPanel(new Dimension(100, 50), modelA);
				GraphTestClient clientA = new GraphTestClient(graphA);
				
				LagGraphTestModel modelB = new LagGraphTestModel();
				LagGraphPanel graphB = new LagGraphPanel(new Dimension(300, 100), modelB);
				GraphTestClient clientB = new GraphTestClient(graphB);
				
				model.run();

		
	}
}

