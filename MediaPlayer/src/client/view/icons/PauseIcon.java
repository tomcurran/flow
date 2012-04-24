package client.view.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.Icon;

public class PauseIcon implements Icon {

	@Override
	public int getIconHeight() {
		return 25;
	}

	@Override
	public int getIconWidth() {
		return 5;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		//Points for the rectangles that makes up the pause button. 
		Point p1 = new Point(5, 5);
        Point p2 = new Point(20, 5);
       
		
        //Turns on antialiasing, making the appearance cleaner. 
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);
		
		//constructs two rectangles for a pause button.
        g.setColor(Color.black);
        g.fillRect(p1.x, p1.y, getIconWidth(), getIconHeight());
        g.fillRect(p2.x, p2.y, getIconWidth(), getIconHeight());
        
	}

}

