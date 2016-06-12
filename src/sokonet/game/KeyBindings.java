package sokonet.game;

import sokonet.KeyPress;
import sokonet.Modifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * TODO
 */
public class KeyBindings {
	private Map<KeyPress, Command> bindings = new HashMap<>();
	private Command defaultCommand;

	/**
	 * TODO
	 *
	 * @param command
	 */
	public void setDefaultCommand(Command command) {
		defaultCommand = command;
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
		if (command == null) {
			command = defaultCommand;
		}

		return Optional.ofNullable(command);
	}
}
