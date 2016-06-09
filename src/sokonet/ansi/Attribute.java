package sokonet.ansi;

public enum Attribute {
	Reset(0), Bright(1), Dim(2), Underscore(4), Blink(5), Reverse(7), Hidden(8),
	Black(30), Red(31), Green(32), Yellow(33), Blue(34), Magenta(35), Cyan(36), White(37),
	BlackBg(40), RedBg(41), GreenBg(42), YellowBg(43), BlueBg(44), MagentaBg(45), CyanBg(46), WhiteBg(47);

	private final int code;

	Attribute(int i) {
		code = i;
	}

	public int code() {
		return code;
	}
}
