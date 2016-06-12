package sokonet.game;

@FunctionalInterface
public interface Command {
	void execute();

	default void undo() {}

	default Command andThen(Command cmd) {
		return new Macro(this, cmd);
	}
}
