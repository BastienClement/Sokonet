package sokonet;

import java.util.Objects;

public class KeyPress {
	private final Key key;
	private final int code;
	private final Modifier mod;

	public KeyPress(Key key, int code, Modifier mod) {
		this.key = key;
		this.code = code;
		this.mod = mod;
	}

	public Key key() {
		return key;
	}

	public int code() {
		return code;
	}

	public Modifier modifier() {
		return mod;
	}

	public KeyPress withModifier(Modifier mod) {
		return new KeyPress(key, code, mod);
	}

	@Override
	public String toString() {
		return mod.prefix() + (key == Key.UNKNOWN ? code : key).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, code, mod);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} if (obj instanceof KeyPress) {
			KeyPress ev = (KeyPress) obj;
			return key == ev.key && code == ev.code && mod == ev.mod;
		} else {
			return false;
		}
	}
}
