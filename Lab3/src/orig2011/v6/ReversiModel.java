package orig2011.v6;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.beans.*;


/**
 * A somewhat defective implementation of the game Reversi. The purpose
 * of this class is to illustrate shortcomings in the game framework.
 * 
 * @author evensen
 * 
 */
public class ReversiModel extends GameUtils {
	public enum Direction {
			EAST(1, 0),
			SOUTHEAST(1, 1),
			SOUTH(0, 1),
			SOUTHWEST(-1, 1),
			WEST(-1, 0),
			NORTHWEST(-1, -1),
			NORTH(0, -1),
			NORTHEAST(1, -1),
			NONE(0, 0);

		private final int xDelta;
		private final int yDelta;

		Direction(final int xDelta, final int yDelta) {
			this.xDelta = xDelta;
			this.yDelta = yDelta;
		}

		public int getXDelta() {
			return this.xDelta;
		}

		public int getYDelta() {
			return this.yDelta;
		}
	}

	public enum Turn {
		BLACK,
		WHITE;

		public static Turn nextTurn(final Turn t) {
			return t == BLACK ? WHITE : BLACK;
		}
	}

	public enum PieceColor {
		BLACK,
		WHITE,
		EMPTY,
		BLACK_CURSOR,
		WHITE_CURSOR,
		EMPTY_CURSOR,
		BLACK_POSSIBLE_CURSOR,
		WHITE_POSSIBLE_CURSOR;

		public static PieceColor opposite(final PieceColor t) {
			return t == BLACK ? WHITE : BLACK;
		}
	}

	/** Graphical representation of a coin. */
	private static final GameTile blackTile = new RoundTile(Color.BLACK,
			Color.BLACK, 1.0, 0.8);
	private static final GameTile whiteTile = new RoundTile(Color.BLACK,
			Color.WHITE, 1.0, 0.8);
	private static final GameTile blankTile = new SquareTile(Color.BLACK,
			new Color(0, 200, 0), 2.0);
	private static final GameTile whiteGridTile = new CompositeTile(blankTile,
			whiteTile);
	private static final GameTile blackGridTile = new CompositeTile(blankTile,
			blackTile);
	private static final GameTile cursorRedTile = new CrossTile(Color.RED, 2.0);
	private static final GameTile cursorBlackTile = new RoundTile(Color.RED,
			new Color(0, 50, 0), 2.0, 0.8);
	private static final GameTile cursorWhiteTile = new RoundTile(Color.RED,
				new Color(210, 255, 210), 2.0, 0.8);
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Turn turn;
	private Position cursorPos;
	private final PieceColor[][] board;
	private int whiteScore;
	private int blackScore;
	private final int width;
	private final int height;
	private boolean gameOver;

	public ReversiModel() {
		this.width = Constants.getGameSize().width;
		this.height = Constants.getGameSize().height;
		this.board = new PieceColor[this.width][this.height];

		// Blank out the whole gameboard...
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				this.board[i][j] = PieceColor.EMPTY;
			}
		}

		this.turn = Turn.BLACK;

		// Insert the four starting bricks.
		int midX = this.width / 2 - 1;
		int midY = this.height / 2 - 1;
		this.board[midX][midY] = PieceColor.WHITE;
		this.board[midX + 1][midY + 1] = PieceColor.WHITE;
		this.board[midX + 1][midY] = PieceColor.BLACK;
		this.board[midX][midY + 1] = PieceColor.BLACK;

		// Set the initial score.
		this.whiteScore = 2;
		this.blackScore = 2;

		this.gameOver = false;

		// Insert the collector in the middle of the gameboard.
		this.cursorPos = new Position(midX, midY);
		updateCursor();
	}

	/**
	 * Return whether the specified position is empty. If it only consists
	 * of a blank tile, it is considered empty.
	 * 
	 * @param pos
	 *            The position to test.
	 * @return true if position is empty, false otherwise.
	 */
//	private boolean isPositionEmpty(final Position pos) {
//		return this.board[pos.getX()][pos.getY()] == PieceColor.EMPTY;
//	}
	
	private boolean isPositionEmpty(final Position pos) {
		PieceColor t =this.board[pos.getX()][pos.getY()];
		return (t==PieceColor.EMPTY || t==PieceColor.BLACK_POSSIBLE_CURSOR || t==PieceColor.WHITE_POSSIBLE_CURSOR );
	}

	/**
	 * Update the direction of the collector
	 * according to the users keypress.
	 * 
	 * @throws GameOverException
	 */
	private Direction updateDirection(final int key) {
		
		switch (key) {
			case KeyEvent.VK_LEFT:
				return Direction.WEST;
			case KeyEvent.VK_UP:
				return Direction.NORTH;
			case KeyEvent.VK_RIGHT:
				return Direction.EAST;
			case KeyEvent.VK_DOWN:
				return Direction.SOUTH;
			case KeyEvent.VK_SPACE:
				tryPlay();
				return Direction.NONE;
			default:
				// Do nothing if another key is pressed
				return Direction.NONE;
		}
		
	}

	private void tryPlay() {
		if (isPositionEmpty(this.cursorPos)) {
			if (canTurn(this.turn, this.cursorPos)) {
				turnOver(this.turn, this.cursorPos);
				this.board[this.cursorPos.getX()][this.cursorPos.getY()] =
						(this.turn == Turn.BLACK
								? PieceColor.BLACK
								: PieceColor.WHITE);
				System.out.println("Bong! White: " + this.whiteScore
						+ "\tBlack: " + this.blackScore);
				this.turn = Turn.nextTurn(this.turn);
			}
			if (!canTurn(this.turn)) {
				if (!canTurn(Turn.nextTurn(this.turn))) {
					this.gameOver = true;
					return;
				}

				this.turn = Turn.nextTurn(this.turn);
			}
		}

	}

	private void turnOver(final Turn turn, final Position cursorPos) {
		if (isPositionEmpty(cursorPos)) {
			PieceColor myColor =
					(turn == Turn.BLACK ? PieceColor.BLACK : PieceColor.WHITE);
			PieceColor opponentColor = PieceColor.opposite(myColor);
			int blackResult = (turn == Turn.BLACK) ? 1 : -1;
			int whiteResult = -blackResult;
			
			this.blackScore += Math.max(0, blackResult);
			this.whiteScore += Math.max(0, whiteResult);

			for (int i = 0; i < 8; i++) {
				Direction d = Direction.values()[i];
				int xDelta = d.getXDelta();
				int yDelta = d.getYDelta();
				int x = cursorPos.getX() + xDelta;
				int y = cursorPos.getY() + yDelta;
				boolean canTurn = false;
				while (x >= 0 && x < this.width && y >= 0 && y < this.height) {
					if (this.board[x][y] == opponentColor) {
						canTurn = true;
					} else if (this.board[x][y] == myColor && canTurn) {
						// Move backwards to the cursor, flipping bricks
						// as we go.
						x -= xDelta;
						y -= yDelta;
						while (!(x == cursorPos.getX() && y == cursorPos.getY())) {
							this.board[x][y] = myColor;
							x -= xDelta;
							y -= yDelta;
							this.blackScore += blackResult;
							this.whiteScore += whiteResult;
						}
						break;
					} else {
						break;
					}
					x += xDelta;
					y += yDelta;
				}
			}
		}
	}

	private boolean canTurn(final Turn turn) {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if (canTurn(turn, new Position(x, y))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean canTurn(final Turn turn, final Position cursorPos) {
		if (isPositionEmpty(cursorPos)) {
			PieceColor myColor =
					(turn == Turn.BLACK ? PieceColor.BLACK : PieceColor.WHITE);
			PieceColor opponentColor = PieceColor.opposite(myColor);
			for (int i = 0; i < 8; i++) {
				Direction d = Direction.values()[i];
				int xDelta = d.getXDelta();
				int yDelta = d.getYDelta();
				int x = cursorPos.getX() + xDelta;
				int y = cursorPos.getY() + yDelta;
				boolean canTurn = false;
				while (x >= 0 && x < this.width && y >= 0 && y < this.height) {
					if (this.board[x][y] == opponentColor) {
						canTurn = true;
					} else if (this.board[x][y] == myColor && canTurn) {
						return true;
					} else {
						break;
					}
					x += xDelta;
					y += yDelta;
				}
			}
		}
		return false;
	}

	/**
	 * Get the current player's color
	 */
	public Turn getTurnColor() {
		return this.turn;
	}

	/**
	 * Accessor to black's current score.
	 * 
	 * @return black's score
	 */
	public int getBlackScore() {
		return this.blackScore;
	}

	/**
	 * Accessor to white's current score.
	 * 
	 * @return white's score
	 */
	public int getWhiteScore() {
		return this.whiteScore;
	}

	/**
	 * Get next position of the collector.
	 */
	private Position getNextCursorPos(final Direction dir) {
		return new Position(this.cursorPos.getX()
					+ dir.getXDelta(),
					this.cursorPos.getY() + dir.getYDelta());
	}

	/**
	 * This method is called repeatedly so that the
	 * game can update its state.
	 * 
	 * @param lastKey
	 *            The most recent keystroke.
	 */
	@Override
	public void gameUpdate(final int lastKey) throws GameOverException {
		pcs.firePropertyChange("Go, fly , Fire!", 1, 2);
		if (!this.gameOver) {
			Direction d=updateDirection(lastKey);
			Position nextCursorPos = getNextCursorPos(d);
			Dimension boardSize = getGameboardSize();
			int nextX =
					Math.max(0,
							Math.min(nextCursorPos.getX(), boardSize.width - 1));
			int nextY =
					Math.max(
							0,
							Math.min(nextCursorPos.getY(), boardSize.height - 1));
			nextCursorPos = new Position(nextX, nextY);
			removeCursor(this.cursorPos);
			this.cursorPos = nextCursorPos;
			updateCursor();
			
			
		} else {
			throw new GameOverException(this.blackScore - this.whiteScore);
		}
	}

	private void removeCursor(final Position oldCursorPos) {
		PieceColor t = this.board[this.cursorPos.getX()][this.cursorPos.getY()];
		
		if(t==PieceColor.BLACK_CURSOR|| t==PieceColor.WHITE_CURSOR||t==PieceColor.WHITE_POSSIBLE_CURSOR||t==PieceColor.BLACK_POSSIBLE_CURSOR){
			
		}
		
		switch (t){
		case BLACK_CURSOR: 	this.board[oldCursorPos.getX()][oldCursorPos.getY()]= PieceColor.BLACK;
										break;
		case WHITE_CURSOR: this.board[oldCursorPos.getX()][oldCursorPos.getY()]= PieceColor.WHITE;
										break;
		case EMPTY_CURSOR: this.board[oldCursorPos.getX()][oldCursorPos.getY()]= PieceColor.EMPTY;
										break;
		case BLACK_POSSIBLE_CURSOR: this.board[oldCursorPos.getX()][oldCursorPos.getY()]= PieceColor.EMPTY;
										break;
		case WHITE_POSSIBLE_CURSOR: this.board[oldCursorPos.getX()][oldCursorPos.getY()]= PieceColor.EMPTY;
										break;
		
		}
		
	}

	private void updateCursor() {
		PieceColor t = this.board[this.cursorPos.getX()][this.cursorPos.getY()];
		if(t == PieceColor.BLACK){this.board[this.cursorPos.getX()][this.cursorPos.getY()] = PieceColor.BLACK_CURSOR;}
		else if(t == PieceColor.WHITE){this.board[this.cursorPos.getX()][this.cursorPos.getY()] = PieceColor.WHITE_CURSOR;}
		else if (canTurn(this.turn, this.cursorPos)) {
			if (this.turn == Turn.BLACK) {
				this.board[cursorPos.getX()][cursorPos.getY()] = PieceColor.BLACK_POSSIBLE_CURSOR;
			} else {
				this.board[cursorPos.getX()][cursorPos.getY()] = PieceColor.WHITE_POSSIBLE_CURSOR;
			}
		} else {
			this.board[cursorPos.getX()][cursorPos.getY()] = PieceColor.EMPTY_CURSOR;
		}
	}
	
	public GameTile getGameboardState(final int x, final int y){
		PieceColor t = this.board[x][y];

		switch (t){
		case BLACK: return blackGridTile;
		case WHITE:	return whiteGridTile;
		case EMPTY:			return blankTile;
		case BLACK_CURSOR: 	return new CompositeTile(blackTile, cursorRedTile);
		case WHITE_CURSOR: 	return new CompositeTile(whiteTile, cursorRedTile);
		case EMPTY_CURSOR: 	return cursorRedTile;
		case BLACK_POSSIBLE_CURSOR: 	return cursorBlackTile;
		case WHITE_POSSIBLE_CURSOR: 	return cursorWhiteTile;
		default: return blankTile;
		}
	}
	
	public GameTile getGameboardState(final Position p){
		return getGameboardState(p.getX(), p.getY());
	}
	
	public void addObserver(PropertyChangeListener l){
		pcs.addPropertyChangeListener(l);
	}
	public void removeObserver(PropertyChangeListener l){
		pcs.removePropertyChangeListener(l);
	}
	
	public int getUpdateSpeed(){
		return 0;
	}

}
