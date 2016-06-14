package sokonet;

/**
 * A game.
 */
public interface Game {
	/**
	 * Called when a key is pressed.
	 *
	 * @param event the key pressed
	 */
	void keyPressed(KeyPress event);

	/**
	 * Called when the display size changes.
	 */
	void displaySizeChanged();
}
