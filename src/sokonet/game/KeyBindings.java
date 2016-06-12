package sokonet.game;

import sokonet.KeyPress;
import sokonet.Modifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KeyBindings {
	private Map<KeyPress, Command> bindings = new HashMap<>();

	public Optional<Command> set(KeyPress key, Command command) {
		return Optional.ofNullable(bindings.put(key, command));
	}

	public Optional<Command> get(KeyPress key) {
		Command command = bindings.get(key);
		if (command == null && key.modifier() != Modifier.NONE) {
			command = bindings.get(key.withModifier(Modifier.NONE));
		}
		return Optional.ofNullable(command);
	}
}
