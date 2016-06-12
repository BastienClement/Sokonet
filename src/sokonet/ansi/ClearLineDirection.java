package sokonet.ansi;

/**
 * The direction in which the current line should be cleared.
 */
public enum ClearLineDirection {
	/**
	 * Clears only the right side of the current line.
	 */
	RIGHT,

	/**
	 * Clears only the left side of the current line.
	 */
	LEFT,

	/**
	 * Clears both the left and right side of the current line.
	 */
	BOTH
}
