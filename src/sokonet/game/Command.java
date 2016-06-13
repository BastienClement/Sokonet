package sokonet.game;

/**
 * A command that can be executed.
 */
@FunctionalInterface
interface Command {
	/**
	 * Executes the command.
	 */
	void execute();

	/**
	 * Constructs a new Command that first executes the action of this command
	 * followed by the action of the given command.
	 *
	 * @param cmd the second command to execute
	 * @return a Macro command executing both command as one
	 */
	default Command andThen(Command cmd) {
		return new Macro(this, cmd);
	}

	/**
	 * Constructs a new named command.
	 *
	 * @param name    the command name
	 * @param command the command being named
	 * @return a named command performing the same action
	 */
	static Command named(String name, Command command) {
		return new Named(name, command);
	}

	/**
	 * A named command.
	 */
	class Named implements Command {
		private String name;
		private Command command;

		Named(String name, Command command) {
			this.name = name;
			this.command = command;
		}

		@Override
		public void execute() {
			command.execute();
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
