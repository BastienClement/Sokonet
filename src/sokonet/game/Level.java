package sokonet.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Predicate;

class Level {
	/**
	 *
	 */
	enum Content {
		Void, Wall, Player, Crate
	}

	/**
	 *
	 */
	class Cell {
		private Content content = Content.Void;
		private boolean target = false;
		private boolean outside = true;

		Content getContent() {
			return content;
		}

		boolean isTarget() {
			return target;
		}

		boolean isOutside() {
			return outside;
		}
	}

	private int index;
	private Cell[][] cells;
	private int px, py;
	private Stack<Command> rewind = new Stack<>();

	/**
	 * @param index
	 * @param width
	 * @param height
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
	 * @param sa
	 * @param sb
	 * @param ea
	 * @param eb
	 * @param da
	 * @param db
	 */
	private void doScanOutside(int sa, int sb,
	                           Predicate<Integer> ea, Predicate<Integer> eb,
	                           int da, int db,
	                           BiFunction<Integer, Integer, Cell> resolver) {
		for (int a = sa; ea.test(a); a += da) {
			for (int b = sb; eb.test(b); b += db) {
				Cell cell = resolver.apply(a, b);
				if (cell.content == Content.Wall) break;
				cell.outside = true;
			}
		}
	}

	/**
	 * TODO
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
	 * @return
	 */
	public int index() {
		return index;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	Cell cell(int x, int y) {
		return cells[x][y];
	}

	/**
	 * @return
	 */
	int width() {
		return cells.length;
	}

	/**
	 * @return
	 */
	int height() {
		return cells[0].length;
	}

	/**
	 * @param pt
	 */
	void setWall(Point pt) {
		setContent(pt, Content.Wall);
	}

	/**
	 * @param pt
	 */
	void setCrate(Point pt) {
		setContent(pt, Content.Crate);
	}

	/**
	 * @param pt
	 */
	void setPlayer(Point pt) {
		px = pt.getX();
		py = pt.getY();
		setContent(pt, Content.Player);
	}

	/**
	 * @param pt
	 */
	void setTarget(Point pt) {
		cells[pt.getX()][pt.getY()].target = true;
	}

	/**
	 * @param pt
	 * @param content
	 */
	private void setContent(Point pt, Content content) {
		cells[pt.getX()][pt.getY()].content = content;
	}

	/**
	 *
	 */
	void rewind() {
		if (!rewind.empty()) {
			rewind.pop().execute();
		}
	}

	/**
	 * @return
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
	 * @return
	 */
	List<Point> up() {
		return move(0, -1);
	}

	/**
	 * @return
	 */
	List<Point> right() {
		return move(1, 0);
	}

	/**
	 * @return
	 */
	List<Point> down() {
		return move(0, 1);
	}

	/**
	 * @return
	 */
	List<Point> left() {
		return move(-1, 0);
	}

	/**
	 * @param dx
	 * @param dy
	 * @return
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
