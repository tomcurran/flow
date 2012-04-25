package client.statistics;

import java.awt.Dimension;


public class GraphTest{

	public static void main (String args[]) {
		

		    	
				GraphTestModel model = new GraphTestModel();
				LineGraphPanel graph = new LineGraphPanel(new Dimension(100, 50), model);
				GraphTestClient client = new GraphTestClient(graph);
				
				model.run();

		
	}
}

