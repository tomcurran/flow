package client.view.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.Icon;

public class SeekForwardIcon implements Icon {

	@Override
	public int getIconHeight() {
		return 5;
	}

	@Override
	public int getIconWidth() {
		return 26;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		//Points for the triangle that makes up the seek button. 
		Point p1 = new Point(5, 5);
		Point p2 = new Point(5, 26);
		Point p3 = new Point(10, 13);
		
		Point p4 = new Point(10, 5);
		Point p5 = new Point(10, 26);
		Point p6 = new Point(23, 13);
		
		Point p7 = new Point(23, 5);
		
				
		//Turns on antialiasing, making the appearance cleaner. 
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		   RenderingHints.VALUE_ANTIALIAS_ON);
				
		//constructs a polygon shaped like a play button.
		int[] xs = { p1.x, p2.x, p3.x };
		int[] ys = { p1.y, p2.y, p3.y };
		Polygon triangle = new Polygon(xs, ys, 3);
		
		int[] xp = { p4.x, p5.x, p6.x };
		int[] yp = { p4.y, p5.y, p6.y };
		Polygon triangle2 = new Polygon(xp, yp, 3);
		
		g.setColor(Color.black);
		g.fillPolygon(triangle);
		g.fillPolygon(triangle2);
		g.fillRect(p7.x, p7.y, getIconWidth(), getIconHeight());
		
	}

}
