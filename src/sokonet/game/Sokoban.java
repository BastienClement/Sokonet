package sokonet.game;

import sokonet.Game;
import sokonet.KeyEvent;
import sokonet.ansi.AnsiDisplay;

public class Sokoban implements Game {
	private AnsiDisplay display;
	private Renderer renderer;

	public Sokoban(AnsiDisplay display) {
		this.display = display;
		this.renderer = new Renderer(display);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		renderer.setStatus(event.key + " <> " + event.code);
	}

	@Override
	public void displaySizeChanged() {
		renderer.sync();
	}
}
