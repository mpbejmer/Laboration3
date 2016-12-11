package orig2011.v2;

import java.awt.Dimension;

import orig2011.v0.Constants;
import orig2011.v0.GameOverException;
import orig2011.v0.GameTile;
import orig2011.v0.Position;

public abstract class GameUtils implements GameModel {
	
	public GameUtils(){
		
	}
	
	/**
	 * Returns the GameTile in logical position (x,y) of the gameboard.
	 * 
	 * @param pos
	 *            The position in the gameboard matrix.
	 */
	public abstract GameTile getGameboardState(final Position pos);
	
	
	/**
	 * Returns the GameTile in logical position (x,y) of the gameboard.
	 * 
	 * @param x
	 *            Coordinate in the gameboard matrix.
	 * @param y
	 *            Coordinate in the gameboard matrix.
	 */
	public abstract GameTile getGameboardState(final int x, final int y);
	
	
	/**
	 * Returns the size of the gameboard.
	 */
	public Dimension getGameboardSize(){
		return Constants.getGameSize();
	}
	
	/**
	 * This method is called repeatedly so that the game can update it's state.
	 * 
	 * @param lastKey
	 *            The most recent keystroke.
	 */
	public abstract void gameUpdate(int lastKey) throws GameOverException;
	
	
}
