package sokonet;

import java.util.Objects;

/**
 * A key press, combination of a Key and Modifier.
 */
public class KeyPress {
	private final Key key;
	private final Modifier modifier;

	/**
	 * Constructs a new key press with the given key and modifier.
	 *
	 * @param key      the key component
	 * @param modifier the modifier component
	 */
	public KeyPress(Key key, Modifier modifier) {
		this.key = key;
		this.modifier = modifier;
	}

	/**
	 * Returns the key component of the key press.
	 *
	 * @return the key
	 */
	public Key key() {
		return key;
	}

	/**
	 * Returns the modifier component of the key press.
	 *
	 * @return the modifier
	 */
	public Modifier modifier() {
		return modifier;
	}

	/**
	 * Returns a new key press with the same key and another modifier.
	 *
	 * @param modifier the new modifier
	 * @return a new key press with the same key and another modifier
	 */
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
		}
		if (obj instanceof KeyPress) {
			KeyPress ev = (KeyPress) obj;
			return key == ev.key && modifier == ev.modifier;
		} else {
			return false;
		}
	}
}
