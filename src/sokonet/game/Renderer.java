package sokonet.game;

import sokonet.ansi.Attribute;
import sokonet.ansi.AnsiDisplay;

class Renderer {
	private AnsiDisplay display;

	private String statusText = "Hello world";
	private Attribute[] statusAttributes = new Attribute[] { Attribute.WhiteBg, Attribute.Black };

	Renderer(AnsiDisplay display) {
		this.display = display;
		sync();
	}

	private void drawStatus() {
		int status_row = display.height() - 1;
		display.setAttribute(statusAttributes);
		display.setCursor(status_row, 2);
		display.write(' ');
		display.write(statusText);
		display.clearLine(AnsiDisplay.ClearLineDirection.RIGHT);
		display.setCursor(status_row, display.width());
		display.setAttribute(Attribute.Reset);
		display.write(' ');
	}

	public void setStatusAttribtues(Attribute... attribtues) {
		statusAttributes = attribtues;
		drawStatus();
	}

	public void setStatus(String text) {
		statusText = text;
		drawStatus();
	}

	public void sync() {
		display.setAttribute(Attribute.Reset);
		display.hideCursor();
		display.clear();

		drawStatus();

		display.flush();
	}
}
