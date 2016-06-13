package sokonet.ansi;

/**
 * A display attribute, used to define text style, color and background.
 */
public enum Attribute {
	// Text style
	Reset(0), Bold(1), Dim(2), Blink(5), Reverse(7), Normal(22), BlinkOff(25),

	// Colors
	BlackColor(30), RedColor(31), GreenColor(32), YellowColor(33), BlueColor(34),
	MagentaColor(35), CyanColor(36), WhiteColor(37), DefaultColor(39),

	// Backgrounds
	BlackBackground(40), RedBackground(41), GreenBackground(42), YellowBackground(43), BlueBackground(44),
	MagentaBackground(45), CyanBackground(46), WhiteBackground(47), DefaultBackground(49);

	private final int code;

	Attribute(int i) {
		code = i;
	}

	/**
	 * Returns the numeric code associated with this attribute.
	 *
	 * @return the numeric code associated with this attribute
	 */
	public int code() {
		return code;
	}
}
