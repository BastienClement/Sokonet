package sokonet.telnet;

import sokonet.Display;
import sokonet.ansi.AnsiDisplay;

import java.io.OutputStream;

class TelnetDisplay extends AnsiDisplay implements Display {
	private int width = 80;
	private int height = 25;

	TelnetDisplay(OutputStream out) {
		super(out);
	}

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
