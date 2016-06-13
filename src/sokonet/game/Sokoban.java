package sokonet.game;

import javafx.util.Pair;
import sokonet.Game;
import sokonet.Key;
import sokonet.KeyPress;
import sokonet.ansi.AnsiDisplay;
import sokonet.ansi.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;

public class Sokoban implements Game {
	private static final KeyBindings BINDINGS_LOCKED = new KeyBindings();

	private static final int DISPLAY_MIN_WIDTH = 80;
	private static final int DISPLAY_MIN_HEIGHT = 24;

	private AnsiDisplay display;
	private Renderer renderer;
	private Stack<KeyBindings> bindings;

	private String history = "";
	private int historyDx = 0;

	private int levelIndex;
	private Level level;

	/**
	 * Constructs a new Sokoban game.
	 *
	 * @param display the display to use for this game
	 */
	public Sokoban(AnsiDisplay display) {
		this.display = display;
		this.renderer = new Renderer(this, display);

		bindings = new Stack<>();
		bindings.push(buildDefaultBindings());
		selectLevel(0);

		if (display.width() < DISPLAY_MIN_WIDTH || display.height() < DISPLAY_MIN_HEIGHT) {
			displaySizeChanged();
		}
	}

	/**
	 * @param index
	 */
	private void selectLevel(int index) {
		level = LevelFactory.getLevel(index);
		levelIndex = index;
		history = "";
		historyDx = 0;
		renderer.setHistory(history, historyDx);
		renderer.setStatusRight("#" + (index + 1));
		renderer.sync();
	}

	/**
	 * @param name
	 * @param move
	 * @return
	 */
	private Command performMovement(String name, Function<Level, List<Point>> move) {
		Command cmd = Command.named(name, () -> {
			try {
				List<Point> delta = move.apply(level);
				renderer.setStatus("");
				renderer.drawLevel(delta);
				if (level.done()) {
					selectLevel(levelIndex + 1);
					renderer.setStatus("Congratulation, you solved level #" + levelIndex);
				}
			} catch (IllegalStateException ex) {
				renderer.setStatus(ex.getMessage());
			}
		});
		return cmd.andThen(() -> {
			if (historyDx < 0)
				history = history.substring(0, history.length() + historyDx);
			history = history + cmd.toString();
			historyDx = 0;
			renderer.setHistory(history, historyDx);
		});
	}

	/**
	 * @param name
	 * @param offset
	 * @return
	 */
	private Command offsetLevel(String name, int offset) {
		return Command.named(name, () -> {
			try {
				selectLevel(levelIndex + offset);
			} catch (IndexOutOfBoundsException ignored) {}
		});
	}

	/**
	 * @return
	 */
	private KeyBindings buildDefaultBindings() {
		KeyBindings binds = new KeyBindings();
		binds.set(Key.W, performMovement("W", Level::up));
		binds.set(Key.A, performMovement("A", Level::left));
		binds.set(Key.S, performMovement("S", Level::down));
		binds.set(Key.D, performMovement("D", Level::right));
		binds.set(Key.R, offsetLevel("RESET", 0));
		binds.set(Key.P, offsetLevel("NEXT", 1));
		binds.set(Key.O, offsetLevel("PREV", -1));
		binds.set(Key.Z, Command.named("REWIND", () -> {
			if (-historyDx < history.length()) {
				level.rewind();
				historyDx--;
				renderer.setHistory(history, historyDx);
				renderer.sync();
			}
		}));
		return binds;
	}

	/**
	 * @return
	 */
	public Optional<Level> level() {
		return Optional.ofNullable(level);
	}

	/**
	 * TODO
	 *
	 * @return
	 */
	public KeyBindings bindings() {
		return bindings.peek();
	}

	/**
	 * TODO
	 *
	 * @param bindings
	 */
	public void setBindings(KeyBindings bindings) {
		popBindings();
		pushBindings(bindings);
	}

	/**
	 * TODO
	 *
	 * @param binds
	 */
	public void pushBindings(KeyBindings binds) {
		bindings.push(binds);
	}

	/**
	 * TODO
	 */
	public void popBindings() {
		bindings.pop();
	}

	/**
	 * Handles key presses. The current KeyBindings object will be used to
	 * transform the key press to a command that can be executed.
	 *
	 * @param key the key pressed by the user
	 */
	@Override
	public void keyPressed(KeyPress key) {
		bindings().get(key).ifPresent(Command::execute);
	}

	/**
	 * Handles display size changes.
	 * The game display must be at least 80x24 character.
	 */
	@Override
	public void displaySizeChanged() {
		if (display.width() < DISPLAY_MIN_WIDTH || display.height() < DISPLAY_MIN_HEIGHT) {
			display.setAttribute(Attribute.BlackBackground, Attribute.WhiteColor);
			display.clear();
			display.setCursor(1, 1);
			display.write("Please resize your terminal window to at least 80x24 characters.");
			if (bindings() != BINDINGS_LOCKED) {
				pushBindings(BINDINGS_LOCKED);
			}
			return;
		} else if (bindings() == BINDINGS_LOCKED) {
			popBindings();
		}

		renderer.setStatusRight(display.width() + " x " + display.height());
		renderer.sync();
	}
}
