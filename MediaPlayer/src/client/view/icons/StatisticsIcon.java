package client.view.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.Icon;

public class StatisticsIcon implements Icon{

	public int getIconWidth() {
        return 20;
	}

	public int getIconHeight() {
        return 20;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
		//Points for the graph axis.
		Point p1 = new Point(5, 5);
		Point p2 = new Point(5, 26);
		Point p3 = new Point(26, 26);
		
		//Points for the line on the graph.
		Point p4 = new Point(5, 15);
		Point p5 = new Point(17, 7);
		Point p6 = new Point(20, 19);
		Point p7 = new Point(26, 9);
		
	    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setColor(Color.black);
	    g.drawLine(p1.x, p1.y, p2.x, p2.y);
	    g.drawLine(p2.x, p2.y, p3.x, p3.y);
	    
	    g.setColor(Color.orange);
	    g.drawLine(p4.x, p4.y, p5.x, p5.y);
	    g.drawLine(p5.x, p5.y, p6.x, p6.y);
	    g.drawLine(p6.x, p6.y, p7.x, p7.y);
	     
	}
}
