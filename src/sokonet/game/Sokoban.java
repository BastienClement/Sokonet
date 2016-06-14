package sokonet.game;

import sokonet.Game;
import sokonet.Key;
import sokonet.KeyPress;
import sokonet.ansi.AnsiDisplay;
import sokonet.ansi.Attribute;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The main game class.
 */
public class Sokoban implements Game {
	// A dummy bindings collection with no commands, used to lock the game
	// if the display size is too small
	private static final KeyBindings BINDINGS_LOCKED = new KeyBindings();

	// Display size limits
	private static final int DISPLAY_MIN_WIDTH = 80;
	private static final int DISPLAY_MIN_HEIGHT = 24;

	private AnsiDisplay display;
	private Renderer renderer;

	private KeyBindings defaultBindings;
	private Stack<KeyBindings> bindings;
	private Set<KeyPress> protectedKeys;

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

		defaultBindings = new KeyBindings();
		defaultBindings.set(Key.W, performMovement("W", Level::up));
		defaultBindings.set(Key.A, performMovement("A", Level::left));
		defaultBindings.set(Key.S, performMovement("S", Level::down));
		defaultBindings.set(Key.D, performMovement("D", Level::right));
		defaultBindings.set(Key.R, offsetLevel("RESET", 0));
		defaultBindings.set(Key.P, offsetLevel("NEXT", 1));
		defaultBindings.set(Key.O, offsetLevel("PREV", -1));
		defaultBindings.set(Key.Z, Command.named("REWIND", () -> {
			if (-historyDx < history.length()) {
				level.rewind();
				historyDx--;
				renderer.setHistory(history, historyDx);
				renderer.sync();
			}
		}));
		defaultBindings.set(Key.M, this::startRecording);
		defaultBindings.set(Key.H, () -> {
			renderer.setStatus("Move: W A S D, Undo: Z, Reset: R, Nav: O P, Quit: ^C");
		});

		protectedKeys = new HashSet<>();
		protectedKeys.addAll(defaultBindings.boundKeys());

		bindings = new Stack<>();
		bindings.push(defaultBindings);
		selectLevel(0);
		renderer.setStatus("Press H for help");

		if (display.width() < DISPLAY_MIN_WIDTH || display.height() < DISPLAY_MIN_HEIGHT) {
			displaySizeChanged();
		}
	}

	/**
	 * Loads a new level.
	 *
	 * @param index the index of the level to load
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
	 * Constructs a command performing a player movement in the current level.
	 *
	 * @param name the name of the command
	 * @param move a function invoking the correct movement method on the level
	 * @return a command that perform the movement
	 */
	private Command performMovement(String name, Function<Level, List<Point>> move) {
		return Command.named(name, () -> {
			try {
				List<Point> delta = move.apply(level);
				renderer.setStatus("");
				renderer.drawLevel(delta);
				if (level.done()) {
					if (levelIndex + 1 >= LevelFactory.getNumLevels()) {
						selectLevel(0);
					} else {
						selectLevel(levelIndex + 1);
					}
					renderer.setStatus("Congratulation, you solved level #" + levelIndex);
				}
				if (historyDx < 0)
					history = history.substring(0, history.length() + historyDx);
				history = history + name;
				historyDx = 0;
				renderer.setHistory(history, historyDx);
			} catch (IllegalStateException ex) {
				renderer.setStatus(ex.getMessage());
			}
		});
	}

	/**
	 * Constructs a command selecting a level based on an offset from the
	 * current one. If called with an offset of 0, produces a command that
	 * resets the current level (by selecting the same one).
	 *
	 * @param name   the name of the command
	 * @param offset the offset of the level to select
	 * @return a command selecting a new level
	 */
	private Command offsetLevel(String name, int offset) {
		return Command.named(name, () -> {
			try {
				selectLevel(levelIndex + offset);
			} catch (IndexOutOfBoundsException ignored) {
			}
		});
	}

	/**
	 * Starts macro recording.
	 * <p>
	 * This methods replace the default key bindings object with a new mapping
	 * that intercept key presses and builds a list of command to execute.
	 * <p>
	 * It is impossible to override basic action keys.
	 */
	private void startRecording() {
		renderer.setStatusAttribtues(Attribute.RedBackground, Attribute.WhiteColor);
		renderer.setStatus("-- RECORDING --");

		Command stopRecording = () -> {
			renderer.setStatusAttribtues(Attribute.WhiteBackground, Attribute.BlackColor);
			renderer.setStatus("");
			popBindings();
		};

		List<Command> macro = new ArrayList<>();

		KeyBindings recorder = new KeyBindings();
		KeyBindings binder = new KeyBindings();

		recorder.setDefaultCommandFactory(k -> () -> defaultBindings.get(k).ifPresent(cmd -> {
			macro.add(cmd);
			renderer.setStatus(macro.stream().map(Command::toString).collect(Collectors.joining(" ")));
		}));

		recorder.set(Key.ESC, stopRecording);
		recorder.set(Key.M, () -> {
			if (macro.isEmpty()) {
				stopRecording.execute();
			} else {
				renderer.setStatus("-- SELECT MACRO KEY --");
				setBindings(binder);
			}
		});

		binder.set(Key.ESC, stopRecording);
		binder.setDefaultCommandFactory(k -> {
			if (protectedKeys.contains(k)) {
				return () -> renderer.setStatus("This key is protected and cannot be bound to a macro");
			} else {
				Command bind = () -> defaultBindings.set(k, Macro.of(macro.toArray(new Command[0])));
				return bind.andThen(stopRecording);
			}
		});

		bindings.push(recorder);
	}

	/**
	 * Returns the current level, if defined.
	 *
	 * @return the current level
	 */
	public Optional<Level> level() {
		return Optional.ofNullable(level);
	}

	/**
	 * Returns the current key bindings collection.
	 *
	 * @return the current key bindings collection
	 */
	public KeyBindings bindings() {
		return bindings.peek();
	}

	/**
	 * Replaces the top-most key bindings on the stack.
	 *
	 * @param bindings the new key bindings to use
	 */
	public void setBindings(KeyBindings bindings) {
		popBindings();
		pushBindings(bindings);
	}

	/**
	 * Pushes a new key bindings collection on the stack.
	 *
	 * @param binds the new key bindings to use
	 */
	public void pushBindings(KeyBindings binds) {
		bindings.push(binds);
	}

	/**
	 * Removes the top-most key bindings collection from the stack.
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
