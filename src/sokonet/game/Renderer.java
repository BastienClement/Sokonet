package sokonet.game;

import sokonet.ansi.Attribute;
import sokonet.ansi.AnsiDisplay;

class Renderer {
	private Sokoban game;
	private AnsiDisplay display;

	private String statusText = "Hello, world!";
	private String statusRightText = "Sokonet 1.0";
	private Attribute[] statusAttributes = new Attribute[] { Attribute.WhiteBg, Attribute.Black };

	Renderer(Sokoban game, AnsiDisplay display) {
		this.display = display;
		sync();
	}

	private void drawStatus() {
		display.atomically(() -> {
			int status_row = display.height() - 1;
			display.setAttribute(statusAttributes);
			display.setCursor(status_row, 2);
			display.write(' ');
			display.write(statusText);
			display.clearLine(AnsiDisplay.ClearLineDirection.RIGHT);
			display.setCursor(status_row, display.width() - statusRightText.length() - 1);
			display.write(statusRightText);
			display.write(' ');
			display.setAttribute(Attribute.Reset);
			display.write(' ');
		});
	}

	public void setStatusAttribtues(Attribute... attribtues) {
		statusAttributes = attribtues;
		drawStatus();
	}

	public void setStatus(String text) {
		statusText = text;
		drawStatus();
	}

	public void setStatusRight(String text) {
		statusRightText = text;
		drawStatus();
	}

	public void sync() {
		display.atomically(() -> {
			display.setAttribute(Attribute.Reset);
			display.hideCursor();
			display.clear();
			drawStatus();
			display.flush();
		});
	}
}
