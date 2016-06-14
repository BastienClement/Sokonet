package sokonet.ansi;

import sokonet.Display;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A display that supports ANSI control sequences and output buffering.
 */
public abstract class AnsiDisplay implements Display {
	private static String ESCAPE = "\u001B";

	private StringBuilder buffer;
	private final OutputStream out;

	/**
	 * Constructs a new ANSI display handler using the given output stream
	 * to writes control sequences.
	 *
	 * @param out the output stream used to output commands
	 */
	public AnsiDisplay(OutputStream out) {
		this.out = out;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(int b) {
		try {
			if (buffer != null) {
				buffer.append((char) b);
			} else {
				out.write(b);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Writes a slice of a byte array on the display.
	 */
	@Override
	public void write(byte[] bytes, int offset, int length) {
		try {
			if (buffer != null) {
				Display.super.write(bytes, offset, length);
			} else {
				out.write(bytes, offset, length);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Sets the cursor to the given position. Top-left position is 1:1.
	 *
	 * @param row the row coordinate
	 * @param col the col coordinate
	 */
	public void setCursor(int row, int col) {
		write(ESCAPE + "[" + row + ";" + col + "H");
	}

	/**
	 * Hides the cursor.
	 */
	public void hideCursor() {
		write(ESCAPE + "[?25l");
	}

	/**
	 * Shows the cursor.
	 */
	public void showCursor() {
		write(ESCAPE + "[?25h");
	}

	/**
	 * Save the cursor position.
	 */
	public void saveCursor() {
		write(ESCAPE + "s");
	}

	/**
	 * Restores the cursor to the previously saved position.
	 */
	public void restoreCursor() {
		write(ESCAPE + "u");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		clear(ClearScreenDirection.BOTH);
	}

	/**
	 * Clears the current line.
	 */
	public void clearLine() {
		clear(ClearLineDirection.BOTH);
	}

	/**
	 * Clears the current line in the given direction.
	 *
	 * @param direction the direction in which the line should be cleared
	 */
	public void clear(ClearLineDirection direction) {
		write(ESCAPE + "[" + direction.ordinal() + "K");
	}

	/**
	 * Clears the screen in the given direction.
	 *
	 * @param direction the direction in which the screen should be cleared
	 */
	public void clear(ClearScreenDirection direction) {
		write(ESCAPE + "[" + direction.ordinal() + "J");
	}

	/**
	 * Sets display attributes.
	 *
	 * @param attrs the attributes to set
	 */
	public void setAttribute(Attribute... attrs) {
		String codes = Arrays.stream(attrs)
		                     .map(attr -> Integer.toString(attr.code()))
		                     .collect(Collectors.joining(";"));
		write(ESCAPE + "[" + codes + "m");
	}

	/**
	 * Executes a runnable block of code atomically.
	 * <p>
	 * All writes operations will be buffered until the end of the runnable
	 * block and written as one operation at the end.
	 *
	 * @param block the block of code to run
	 */
	public void atomically(Runnable block) {
		StringBuilder old = buffer;
		StringBuilder buf = buffer = new StringBuilder();
		block.run();
		buffer = old;
		write(buf.toString());
	}
}
