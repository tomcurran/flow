package client.view.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.Icon;

public class PlayIcon implements Icon {

	@Override
	public int getIconHeight() {
		return 0;
	}

	@Override
	public int getIconWidth() {
		return 0;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		//Points for the triangle that makes up the play button. 
		Point p1 = new Point(5, 5);
        Point p2 = new Point(5, 25);
        Point p3 = new Point(25, 15);
		
        //Turns on antialiasing, making the appearance cleaner. 
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);
		
		//constructs a polygon shaped like a play button.
		int[] xs = { p1.x, p2.x, p3.x };
        int[] ys = { p1.y, p2.y, p3.y };
        Polygon triangle = new Polygon(xs, ys, 3);
        g.setColor(Color.black);
        g.fillPolygon(triangle);
        
	}

}
