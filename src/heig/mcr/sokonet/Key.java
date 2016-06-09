package heig.mcr.sokonet;

public enum Key {
	A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
	UNKNOWN;

	public static Key forCode(int code) {
		int idx = code - 97;
		if (idx < 0 || idx > 25) return UNKNOWN;
		return values()[idx];
	}
}
