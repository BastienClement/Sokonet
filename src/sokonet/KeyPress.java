package sokonet;

import java.util.Objects;

public class KeyPress {
	private final Key key;
	private final Modifier modifier;

	public KeyPress(Key key, Modifier modifier) {
		this.key = key;
		this.modifier = modifier;
	}

	public Key key() {
		return key;
	}

	public Modifier modifier() {
		return modifier;
	}

	public KeyPress withModifier(Modifier modifier) {
		return key.withModifier(modifier);
	}

	@Override
	public String toString() {
		return modifier.prefix() + key.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, modifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} if (obj instanceof KeyPress) {
			KeyPress ev = (KeyPress) obj;
			return key == ev.key && modifier == ev.modifier;
		} else {
			return false;
		}
	}
}
