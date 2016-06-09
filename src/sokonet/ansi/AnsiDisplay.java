package sokonet.ansi;

import sokonet.Display;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AnsiDisplay implements Display {
	private final OutputStream out;
	public AnsiDisplay(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) {
		try {
			out.write(b);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void write(byte[] bytes, int offset, int length) {
		try {
			out.write(bytes, offset, length);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void clear() {
		write("\u001B[2J");
	}

	public void setCursor(int x, int y) {
		write("\u001B[" + x + ";" + y + "H");
	}

	public void hideCursor() {
		write("\u001B[?25l");
	}

	public void showCursor() {
		write("\u001B[?25h");
	}

	public enum ClearLineDirection { BOTH, LEFT, RIGHT }
	public void clearLine(ClearLineDirection direction) {
		switch (direction) {
			case RIGHT: write("\u001B[0K"); break;
			case LEFT: write("\u001B[1K"); break;
			case BOTH: write("\u001B[2K"); break;
		}
	}

	public void setAttribute(Attribute... attrs) {
		String codes = Arrays.stream(attrs).map(Attribute::code).map(Object::toString).collect(Collectors.joining(";"));
		write("\u001B[" + codes + "m");
	}
}
