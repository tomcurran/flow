package client.statistics;

import java.awt.Dimension;


public class LineGraphTest{

	public static void main (String args[]) {
 	
				LineGraphTestModel modelA = new LineGraphTestModel();
				LineGraphPanel graphA = new LineGraphPanel(new Dimension(100, 50), modelA);
				GraphTestClient clientA = new GraphTestClient(graphA);
			
				modelA.run();
	}
}

