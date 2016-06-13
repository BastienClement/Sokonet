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
 * TODO
 */
public class KeyBindings {
	private Map<KeyPress, Command> bindings = new HashMap<>();
	private Function<KeyPress, Command> defaultCommandFactory;

	/**
	 * TODO
	 *
	 * @param factory
	 */
	public void setDefaultCommandFactory(Function<KeyPress, Command> factory) {
		defaultCommandFactory = factory;
	}

	/**
	 * TODO
	 *
	 * @param command
	 */
	public void setDefaultCommand(Command command) {
		defaultCommandFactory = k -> command;
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @param command
	 * @return
	 */
	public Optional<Command> set(Key key, Command command) {
		return set(key.withModifier(Modifier.NONE), command);
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @param command
	 * @return
	 */
	public Optional<Command> set(KeyPress key, Command command) {
		return Optional.ofNullable(bindings.put(key, command));
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @return
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
	 *
	 * @return
	 */
	public Set<KeyPress> boundKeys() {
		return bindings.keySet();
	}
}
