package sokonet;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public interface Display {
	int width();

	int height();

	void write(int c);

	default void write(byte[] str, int offset, int length) {
		for (int i = offset; i < offset + length; i++) {
			write(str[i]);
		}
	}

	default void write(byte[] bytes) {
		write(bytes, 0, bytes.length);
	}

	default void write(String str) {
		write(str.getBytes(StandardCharsets.US_ASCII));
	}

	default void write(int c, int length) {
		byte[] buffer = new byte[length];
		Arrays.fill(buffer, (byte) c);
		write(buffer);
	}

	void flush();

	void clear();
}
