package sokonet;

public enum Key {
	A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
	ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
	TAB, SPACE, ENTER, BACKSPACE, ESC,
	UNKNOWN;

	public static Key forCode(int code) {
		if (code >= 48 && code <= 57) {
			return values()[code-22];
		} else {
			int idx = code - 97;
			if (idx < 0 || idx > 25) return UNKNOWN;
			return values()[idx];
		}
	}

	public KeyPress withModifier(Modifier modifier) {
		return new KeyPress(this, modifier);
	}
}
