package heig.mcr.sokonet.telnet;

/**
 * https://support.microsoft.com/en-us/kb/231866
 */
enum TelnetOpcode {
	ECHO(1), SUPPRESS_GO_AHEAD(3), STATUS(5), TIMING_MARK(6), TERMINAL_TYPE(24), WINDOW_SIZE(31),
	TERMINAL_SPEED(32), REMOTE_FLOW_CONTROL(33), LINEMODE(34), ENVIRONMENT_VARIABLE(36),
	SE(240), NOP(241), BRK(243), IP(244), AO(245), AYT(246), EC(247), EL(248), GA(249), SB(250),
	WILL(251), WONT(252), DO(253), DONT(254), IAC(255);

	private static TelnetOpcode[] opcodes = new TelnetOpcode[256];

	static {
		for (TelnetOpcode opcode : values()) {
			opcodes[opcode.intCode] = opcode;
		}
	}

	public final byte code;
	private final int intCode;

	TelnetOpcode(int code) {
		this.intCode = code;
		this.code = (byte) code;
	}

	public static TelnetOpcode fromByte(int b) {
		return opcodes[b & 0xff];
	}
}
