package sokonet.game;

/**
 * A command that can be executed.
 */
@FunctionalInterface
interface Command {
	/**
	 * TODO
	 */
	void execute();

	/**
	 * TODO
	 *
	 * @param cmd
	 * @return
	 */
	default Command andThen(Command cmd) {
		return new Macro(this, cmd);
	}

	/**
	 * Constructs a new named command.
	 *
	 * @param name
	 * @param command
	 * @return
	 */
	static Command named(String name, Runnable command) {
		return new Named(name, command);
	}

	/**
	 * A named command.
	 */
	class Named implements Command {
		private String name;
		private Runnable command;

		Named(String name, Runnable command) {
			this.name = name;
			this.command = command;
		}

		@Override
		public void execute() {
			command.run();
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
