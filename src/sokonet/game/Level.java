package sokonet.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A Sokoban level.
 * Handles the state of the game and movements.
 */
class Level {
	/**
	 * The content of a level cell.
	 */
	enum Content {
		Void, Wall, Player, Crate
	}

	/**
	 * A level cell.
	 */
	class Cell {
		private Content content = Content.Void;
		private boolean target = false;
		private boolean outside = true;

		/**
		 * Returns the content of the cell.
		 *
		 * @return the content of the cell
		 */
		Content getContent() {
			return content;
		}

		/**
		 * Checks if the cell is a target.
		 *
		 * @return true if the cell is a target, false otherwise
		 */
		boolean isTarget() {
			return target;
		}

		/**
		 * Checks if the cell is outside the level walls.
		 *
		 * @return true if the cell is outside the level wall
		 */
		boolean isOutside() {
			return outside;
		}
	}

	private int index;
	private Cell[][] cells;
	private int px, py;
	private Stack<Command> rewind = new Stack<>();

	/**
	 * Constructs a new level of the given size.
	 *
	 * @param index  the index of the level
	 * @param width  the width of the level
	 * @param height the height of the level
	 */
	Level(int index, int width, int height) {
		this.index = index;
		cells = new Cell[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				cells[x][y] = new Cell();
			}
		}
	}

	/**
	 * Performs a flood fill of the inside of the level.
	 */
	void scanInside() {
		scanInside(px, py);
	}

	private void scanInside(int x, int y) {
		if (x >= cells.length || x < 0) return;
		if (y >= cells[0].length || y < 0) return;
		Cell cell = cells[x][y];
		if (!cell.outside || cell.content == Content.Wall) return;
		cell.outside = false;
		scanInside(x + 1, y);
		scanInside(x - 1, y);
		scanInside(x, y + 1);
		scanInside(x, y - 1);
	}

	/**
	 * Returns the index of this level.
	 *
	 * @return the index of the level
	 */
	public int index() {
		return index;
	}

	/**
	 * Retrieves the cell at the given coordinates.
	 *
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return the requested cell
	 */
	Cell cell(int x, int y) {
		return cells[x][y];
	}

	/**
	 * Returns the width of the level.
	 *
	 * @return the width of the level
	 */
	int width() {
		return cells.length;
	}

	/**
	 * Returns the height of the level.
	 *
	 * @return the height of the level
	 */
	int height() {
		return cells[0].length;
	}

	/**
	 * Defines a new wall.
	 *
	 * @param pt the wall coordinates
	 */
	void setWall(Point pt) {
		setContent(pt, Content.Wall);
	}

	/**
	 * Defines a new crate.
	 *
	 * @param pt the crate coordinates
	 */
	void setCrate(Point pt) {
		setContent(pt, Content.Crate);
	}

	/**
	 * Sets the player starting point.
	 *
	 * @param pt the player starting point
	 */
	void setPlayer(Point pt) {
		px = pt.getX();
		py = pt.getY();
		setContent(pt, Content.Player);
	}

	/**
	 * Sets a new target cell.
	 *
	 * @param pt the target cell coordinates
	 */
	void setTarget(Point pt) {
		cells[pt.getX()][pt.getY()].target = true;
	}

	/**
	 * Sets the content of a cell.
	 *
	 * @param pt      the coordinates of the cell
	 * @param content the content to put in the cell
	 */
	private void setContent(Point pt, Content content) {
		cells[pt.getX()][pt.getY()].content = content;
	}

	/**
	 * Rewind the last action from the history.
	 */
	void rewind() {
		if (!rewind.empty()) {
			rewind.pop().execute();
		}
	}

	/**
	 * Checks if the level is done.
	 *
	 * @return true if the level is done
	 */
	boolean done() {
		for (Cell[] col : cells) {
			for (Cell cell : col) {
				if (cell.target && cell.content != Content.Crate) return false;
			}
		}
		return true;
	}

	/**
	 * Moves up.
	 *
	 * @return a list of coordinates of cells affected by the move
	 */
	List<Point> up() {
		return move(0, -1);
	}

	/**
	 * Moves right.
	 *
	 * @return a list of coordinates of cells affected by the move
	 */
	List<Point> right() {
		return move(1, 0);
	}

	/**
	 * Moves down.
	 *
	 * @return a list of coordinates of cells affected by the move
	 */
	List<Point> down() {
		return move(0, 1);
	}

	/**
	 * Moves left.
	 *
	 * @return a list of coordinates of cells affected by the move
	 */
	List<Point> left() {
		return move(-1, 0);
	}

	/**
	 * Moves.
	 *
	 * @param dx movement on the x-axis
	 * @param dy movement on the y-axis
	 * @return a list of coordinates of cells affected by the move
	 */
	private List<Point> move(int dx, int dy) {
		List<Point> altered = new ArrayList<>(3);

		int x = px + dx;
		int y = py + dy;

		Command undo = () -> {
			cells[px][py].content = Content.Void;
			px -= dx;
			py -= dy;
			cells[px][py].content = Content.Player;
		};

		switch (cells[x][y].content) {
			case Wall:
				throw new IllegalStateException("Invalid move!");

			case Crate:
				int xx = x + dx;
				int yy = y + dy;
				if (cells[xx][yy].content != Content.Void) {
					throw new IllegalStateException("Something is blocking this crate!");
				} else {
					cells[xx][yy].content = Content.Crate;
					altered.add(new Point(xx, yy));
					undo = undo.andThen(() -> {
						cells[xx][yy].content = Content.Void;
						cells[xx - dx][yy - dy].content = Content.Crate;
					});
				}
				break;
		}

		cells[x][y].content = Content.Player;
		cells[px][py].content = Content.Void;

		altered.add(new Point(x, y));
		altered.add(new Point(px, py));

		px = x;
		py = y;

		rewind.push(undo);
		return altered;
	}
}
