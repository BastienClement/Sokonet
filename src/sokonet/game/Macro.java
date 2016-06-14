package sokonet.game;

import java.util.Arrays;

/**
 * A compound command.
 */
class Macro implements Command {
	/**
	 * Builds a new macro from the given commands.
	 *
	 * @param commands the commands to run with this macro
	 * @return a new macro executing the given commands
	 */
	public static Macro of(Command... commands) {
		return new Macro(Arrays.copyOf(commands, commands.length));
	}

	private Command[] commands;

	Macro(Command... commands) {
		this.commands = commands;
	}

	@Override
	public void execute() {
		for (Command command : commands) {
			command.execute();
		}
	}

	@Override
	public Command andThen(Command cmd) {
		Command[] cmds = Arrays.copyOf(commands, commands.length + 1);
		cmds[cmds.length - 1] = cmd;
		return new Macro(cmds);
	}

	@Override
	public String toString() {
		return "[MACRO]";
	}
}
