package sokonet.ansi;

/**
 * The direction in which the screen should be cleared.
 */
public enum ClearScreenDirection {
	/**
	 * Clears only the bottom side of the display.
	 */
	DOWN,

	/**
	 * Clears only the top side of the display.
	 */
	UP,

	/**
	 * Clears both the top and bottom side of the display.
	 */
	BOTH
}
