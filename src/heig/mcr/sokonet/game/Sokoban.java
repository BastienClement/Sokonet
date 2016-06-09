package heig.mcr.sokonet.game;

import heig.mcr.sokonet.Display;
import heig.mcr.sokonet.Game;
import heig.mcr.sokonet.KeyEvent;

public class Sokoban implements Game {
	private Display display;

	public Sokoban(Display display) {
		this.display = display;
		display.clear();
		displaySizeChanged();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		System.out.println(event.key + " <> " + event.code);
	}

	@Override
	public void displaySizeChanged() {
		System.out.println("Display size: " + display.width() + "x" + display.height());
	}
}
