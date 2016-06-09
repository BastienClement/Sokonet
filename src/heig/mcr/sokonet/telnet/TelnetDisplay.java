package heig.mcr.sokonet.telnet;

import heig.mcr.sokonet.Display;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

class TelnetDisplay implements Display {
	private int width = 80;
	private int height = 24;

	private OutputStream out;

	TelnetDisplay(OutputStream out) {
		this.out = out;
	}

	private void write(String str) {
		try {
			out.write(str.getBytes(StandardCharsets.US_ASCII));
			out.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
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

	@Override
	public void clear() {
		write("\u001B[2J");
	}
}
