package sokonet.telnet;

import sokonet.Display;
import sokonet.ansi.AnsiDisplay;

import java.io.OutputStream;

/**
 * A Telnet display.
 * Implements size related operations.
 */
class TelnetDisplay extends AnsiDisplay implements Display {
	private int width = 80;
	private int height = 24;

	TelnetDisplay(OutputStream out) {
		super(out);
	}

	/**
	 * Sets the display size.
	 *
	 * @param width  the display width
	 * @param height the display height
	 */
	void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}
}
