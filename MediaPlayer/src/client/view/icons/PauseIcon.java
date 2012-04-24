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
		return 20;
	}

	@Override
	public int getIconWidth() {
		return 5;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
        //Turns on antialiasing, making the appearance cleaner. 
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);
		System.out.println(x);
		//constructs two rectangles for a pause button.
        g.setColor(Color.black);
        g.fillRect(7, 5, getIconWidth(), getIconHeight());
        g.fillRect(17, 5, getIconWidth(), getIconHeight());
        
	}

}

