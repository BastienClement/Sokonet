package sokonet.game;

import java.util.Collections;
import java.util.List;

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
		private boolean outside = false;

		public Content getContent() {
			return content;
		}

		public boolean isTarget() {
			return target;
		}

		public boolean isOutside() {
			return outside;
		}
	}

	private int index;
	private Cell[][] cells;
	private int px, py;

	/**
	 *
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
	 * TODO
	 */
	void scanOutsides() {
		for (Cell[] col : cells) {
			for (int i = 0; i < col.length; i++) {
				if (col[i].getContent() == Content.Wall) break;
				col[i].outside = true;
			}
			for (int i = col.length - 1; i > 0; i--) {
				if (col[i].getContent() == Content.Wall) break;
				col[i].outside = true;
			}

		}
	}

	/**
	 *
	 * @return
	 */
	public int index() {
		return index;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	Cell cell(int x, int y) {
		return cells[x][y];
	}

	/**
	 *
	 * @return
	 */
	int width() {
		return cells.length;
	}

	/**
	 *
	 * @return
	 */
	int height() {
		return cells[0].length;
	}

	/**
	 *
	 * @param pt
	 */
	void setWall(Point pt) {
		setContent(pt, Content.Wall);
	}

	/**
	 *
	 * @param pt
	 */
	void setCrate(Point pt) {
		setContent(pt, Content.Crate);
	}

	/**
	 *
	 * @param pt
	 */
	void setPlayer(Point pt) {
		px = pt.getX();
		py = pt.getY();
		setContent(pt, Content.Player);
	}

	/**
	 *
	 * @param pt
	 */
	void setTarget(Point pt) {
		cells[pt.getX()][pt.getY()].target = true;
	}

	/**
	 *
	 * @param pt
	 * @param content
	 */
	private void setContent(Point pt, Content content) {
		cells[pt.getX()][pt.getY()].content = content;
	}

	/**
	 *
	 * @return
	 */
	List<Point> up() {
		return move(0, -1);
	}

	/**
	 *
	 * @return
	 */
	List<Point> right() {
		return move(1, 0);
	}

	/**
	 *
	 * @return
	 */
	List<Point> down() {
		return move(0, 1);
	}

	/**
	 *
	 * @return
	 */
	List<Point> left() {
		return move(-1, 0);
	}

	/**
	 *
	 * @param dx
	 * @param dy
	 * @return
	 */
	private List<Point> move(int dx, int dy) {
		return Collections.emptyList();
	}
}
