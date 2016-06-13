package sokonet.game;

import sokonet.Key;
import sokonet.KeyPress;
import sokonet.Modifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A key bindings collection.
 * <p>
 * Key bindings associate key presses with actions.
 */
public class KeyBindings {
	private Map<KeyPress, Command> bindings = new HashMap<>();
	private Function<KeyPress, Command> defaultCommandFactory;

	/**
	 * Defines the default command factory.
	 * <p>
	 * The factory is called with the requested key press if no matching
	 * bindings are defined in this collection.
	 *
	 * @param factory a method constructing command from key presses
	 */
	public void setDefaultCommandFactory(Function<KeyPress, Command> factory) {
		defaultCommandFactory = factory;
	}

	/**
	 * Defines the default command.
	 * <p>
	 * The default command is returned if no matching bindings are
	 * defined for a specific key presses.
	 *
	 * @param command the default command to return
	 */
	public void setDefaultCommand(Command command) {
		defaultCommandFactory = k -> command;
	}

	/**
	 * Defines a key binding.
	 *
	 * @param key     the key to bind to the command
	 * @param command the command
	 * @return the previous command bound to this key, if it exists
	 */
	public Optional<Command> set(Key key, Command command) {
		return set(key.withModifier(Modifier.NONE), command);
	}

	/**
	 * Defines a key binding.
	 *
	 * @param key     the key to bind to the command
	 * @param command the command
	 * @return the previous command bound to this key, if it exists
	 */
	public Optional<Command> set(KeyPress key, Command command) {
		return Optional.ofNullable(bindings.put(key, command));
	}

	/**
	 * Returns the command corresponding to a given key.
	 * <p>
	 * If no bindings are defined for a key press including modifiers,
	 * this method will attempt to perform the matching with the key
	 * without modifiers.
	 * <p>
	 * If no matching bindings can be found, the default factory is
	 * invoked, if defined.
	 *
	 * @param key the key to look for
	 * @return the command corresponding to the given key, if it exists
	 */
	public Optional<Command> get(KeyPress key) {
		// Exact command
		Command command = bindings.get(key);

		// Without modifier
		if (command == null && key.modifier() != Modifier.NONE) {
			command = bindings.get(key.withModifier(Modifier.NONE));
		}

		// Default
		if (command == null && defaultCommandFactory != null) {
			command = defaultCommandFactory.apply(key);
		}

		return Optional.ofNullable(command);
	}

	/**
	 * Returns the set of keys bound in this collection.
	 *
	 * @return the set of keys bound in this collection
	 */
	public Set<KeyPress> boundKeys() {
		return bindings.keySet();
	}
}
