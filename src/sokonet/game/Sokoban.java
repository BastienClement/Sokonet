package sokonet.game;

import sokonet.Game;
import sokonet.KeyPress;
import sokonet.ansi.AnsiDisplay;
import sokonet.ansi.Attribute;

import java.util.function.Supplier;

public class Sokoban implements Game {
	private AnsiDisplay display;
	private Renderer renderer;
	private KeyBindings bindings;

	/**
	 * Constructs a new Sokoban game.
	 *
	 * @param display the display to use for this game
	 */
	public Sokoban(AnsiDisplay display) {
		this.display = display;
		this.renderer = new Renderer(this, display);
		this.bindings = new KeyBindings();
	}

	/**
	 * Constructs the default command, invoked if no commands are bound to the
	 * key pressed by the user. The default action is to display a notice in
	 * the status bar.
	 *
	 * @param key the key pressed by the user
	 * @return a supplier of a command that display the key in the status bar
	 */
	private Supplier<Command> undefinedCommand(KeyPress key) {
		return () -> () -> renderer.setStatus("Unknown command: " + key.toString());
	}

	/**
	 * Handles key presses. The current KeyBindings object will be used to
	 * transform the key press to a command that can be executed. If no
	 * commands are bound to the key, a notice is displayed in the status bar.
	 *
	 * @param key the key pressed by the user
	 */
	@Override
	public void keyPressed(KeyPress key) {
		bindings.get(key).orElseGet(undefinedCommand(key)).execute();
	}

	/**
	 * Handles display size changes.
	 * The game display must be at least 80x24 character.
	 */
	@Override
	public void displaySizeChanged() {
		if (display.width() < 80 || display.height() < 24) {
			display.setAttribute(Attribute.BlackBackground, Attribute.WhiteColor);
			display.clear();
			display.write("Please resize your terminal window to at least 80x24 characters.");
			return;
		}

		renderer.setStatusRight(display.width() + " x " + display.height());
		renderer.sync();
	}
}
