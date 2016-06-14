package sokonet.game;

import java.util.Objects;

/**
 * A 2D point.
 */
public class Point {
	private int x;
	private int y;

	/**
	 * Constructs a new 2D point with the given coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the point x coordinate.
	 *
	 * @return the point x coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the point y coordinate.
	 *
	 * @return the point y coordinate
	 */
	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Point) {
			Point p = (Point) obj;
			return x == p.x && y == p.y;
		} else {
			return false;
		}
	}
}
