package sokonet.game;

import java.util.Arrays;

class Macro implements Command {
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
}
