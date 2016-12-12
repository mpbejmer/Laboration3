package orig2011.v4;

import java.awt.Dimension;
import java.awt.Graphics;

public class BlankTile implements GameTile {
	
	/**
	 * Draws itself in a given graphics context, position and size.
	 * 
	 * @param g
	 *            graphics context to draw on.
	 * @param x
	 *            pixel x coordinate of the tile to be drawn.
	 * @param y
	 *            pixel y coordinate of the tile to be drawn.
	 * @param d
	 *            size of this object in pixels.
	 */
	public void draw(Graphics g, int x, int y, Dimension d) {
		// The default GameTile is transparent,
		// therefore no drawing is performed.
	}

}
