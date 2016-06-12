package sokonet;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * An abstract display on which text can be written.
 */
public interface Display {
	/**
	 * Returns the width of the display, if supported.
	 *
	 * @return the width of the display
	 */
	int width();

	/**
	 * Returns the height of the display, if supported.
	 *
	 * @return the height of the display
	 */
	int height();

	/**
	 * Writes a single char on the display.
	 *
	 * @param c the character to write
	 */
	void write(int c);

	/**
	 * Writes a slice of a byte array on the display.
	 * <p>
	 * The default implementation calls {@code write(int)} on each byte from
	 * the source buffer.
	 *
	 * @param bytes  the source buffer to write
	 * @param offset the offset of the first byte to write
	 * @param length the number of bytes to write
	 */
	default void write(byte[] bytes, int offset, int length) {
		for (int i = offset; i < offset + length; i++) {
			write(bytes[i]);
		}
	}

	/**
	 * Writes a byte array on the display.
	 * <p>
	 * The default implementation calls {@code write(byte[], int, int)} with
	 * {@literal 0} as {@code offset} and the given buffer length as
	 * {@code length},
	 *
	 * @param bytes the source buffer to write
	 */
	default void write(byte[] bytes) {
		write(bytes, 0, bytes.length);
	}

	/**
	 * Writes a string on the display.
	 * <p>
	 * The default implementation calls {@code write(byte[])} with the bytes
	 * from the given string, using US_ASCII as the charset.
	 *
	 * @param str the string to write
	 */
	default void write(String str) {
		write(str.getBytes(StandardCharsets.US_ASCII));
	}

	/**
	 * Writes the same character multiple times.
	 * <p>
	 * The default implementation calls {@code write(byte[])} with an array
	 * of the requested length, filled with the given character.
	 *
	 * @param c      the character to write
	 * @param length how many times the character should be written
	 */
	default void write(int c, int length) {
		byte[] buffer = new byte[length];
		Arrays.fill(buffer, (byte) c);
		write(buffer);
	}

	/**
	 * Flushes internal buffers.
	 */
	void flush();

	/**
	 * Clears the display.
	 */
	void clear();
}
