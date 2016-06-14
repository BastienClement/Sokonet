package sokonet;

/**
 * A keyboard modifier.
 */
public enum Modifier {
	NONE(""), SHIFT("SHIFT-"), CTRL("CTRL-");

	private String prefix;

	Modifier(String prefix) {
		this.prefix = prefix;
	}

	public String prefix() {
		return prefix;
	}
}
