package heig.mcr.sokonet;

@SuppressWarnings("WeakerAccess")
public class KeyEvent {
	public final Key key;
	public final int code;
	public final boolean shift;
	public final boolean ctrl;

	public KeyEvent(Key key, int code, boolean shift, boolean ctrl) {
		this.key = key;
		this.code = code;
		this.shift = shift;
		this.ctrl = ctrl;
	}
}
