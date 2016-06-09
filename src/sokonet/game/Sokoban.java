package sokonet.game;

import sokonet.Display;
import sokonet.Game;
import sokonet.Key;
import sokonet.KeyEvent;
import sokonet.ansi.AnsiDisplay;
import sokonet.ansi.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sokoban implements Game {
	private enum KeyState {
		Normal, Recording, Bind
	}

	private Display display;
	private Renderer renderer;

	private KeyState keyState = KeyState.Normal;
	private List<KeyEvent> macro;

	public Sokoban(AnsiDisplay display) {
		this.display = display;
		this.renderer = new Renderer(this, display);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		switch (keyState) {
			case Recording:
				if (event.key == Key.M) {
					renderer.setStatus("-- PRESS KEY FOR MACRO --");
					keyState = KeyState.Bind;
				} else {
					macro.add(event);
					renderer.setStatus(macro.stream().map(KeyEvent::toString).collect(Collectors.joining(" > ")));
				}
				break;

			case Bind:
				switch (event.key) {
					case W:
					case A:
					case S:
					case D:
					case M:
						renderer.setStatus("Binding macro to this key is not allowed");
						break;

					default:
						renderer.setStatus("Macro bound to " + event.toString());

					case ESC:
						if (event.key == Key.ESC) renderer.setStatus("Macro binding canceled");
						renderer.setStatusAttribtues(Attribute.WhiteBg, Attribute.Black);
						keyState = KeyState.Normal;
						macro = null;
				}
				break;

			case Normal:
				renderer.setStatusRight(event.toString());
				switch (event.key) {
					case M:
						macro = new ArrayList<>();
						renderer.setStatusAttribtues(Attribute.RedBg, Attribute.White);
						renderer.setStatus("-- RECORDING MACRO --");
						renderer.setStatusRight("");
						keyState = KeyState.Recording;
						break;

					default:
						renderer.setStatus("Unknown command: " + event.toString());
				}
				break;
		}
	}

	@Override
	public void displaySizeChanged() {
		renderer.setStatusRight(display.width() + " x " + display.height());
		renderer.sync();
	}
}
