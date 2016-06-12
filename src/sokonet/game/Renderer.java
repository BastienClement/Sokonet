package sokonet.game;

import sokonet.ansi.Attribute;
import sokonet.ansi.AnsiDisplay;
import sokonet.ansi.ClearLineDirection;
import sokonet.ansi.ClearScreenDirection;

class Renderer {
	private Sokoban game;
	private AnsiDisplay display;

	private String statusText = "Hello, world!";
	private String statusRightText = "Sokonet 1.0";
	private Attribute[] statusAttributes = new Attribute[] { Attribute.WhiteBackground, Attribute.BlackColor};

	Renderer(Sokoban game, AnsiDisplay display) {
		this.display = display;
		sync();
	}

	private String limit(String in, int length) {
		if (in.length() > length) {
			return in.substring(0, length);
		} else {
			return in;
		}
	}

	private void drawStatus() {
		display.atomically(() -> {
			int status_row = display.height() - 1;
			int status_width = display.width() - 4;

			// Clear the last 3 lines of the screen
			display.setCursor(status_row - 1, 1);
			display.setAttribute(Attribute.Reset);
			display.clear(ClearScreenDirection.DOWN);

			// Left side
			display.setAttribute(statusAttributes);
			display.setCursor(status_row, 2);
			display.write(' ');
			display.write(limit(statusText, status_width));
			display.clear(ClearLineDirection.RIGHT);

			// Right side
			display.setCursor(status_row, Math.max(display.width() - statusRightText.length() - 1, 2));
			display.write(limit(statusRightText, status_width));
			display.write(' ');

			// Status bar end
			display.setAttribute(Attribute.Reset);
			display.write(' ');
			display.setCursor(status_row + 1, 1);
			display.clearLine();
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
