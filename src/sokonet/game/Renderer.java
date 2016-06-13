package sokonet.game;

import sokonet.ansi.AnsiDisplay;
import sokonet.ansi.Attribute;
import sokonet.ansi.ClearLineDirection;
import sokonet.ansi.ClearScreenDirection;

import java.util.Collections;
import java.util.List;

class Renderer {
	private Sokoban game;
	private AnsiDisplay display;

	private String statusText = "Hello, world!";
	private String statusRightText = "Sokonet 1.0";
	private Attribute[] statusAttributes = new Attribute[] { Attribute.WhiteBackground, Attribute.BlackColor};
	private String history = ".";
	private int historyDx = 0;
	private Attribute[] historyAttributes = new Attribute[] { Attribute.WhiteBackground, Attribute.BlackColor};

	private int levelCursorX, levelCursorY;
	private Attribute levelColor;

	Renderer(Sokoban game, AnsiDisplay display) {
		this.game = game;
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

	private void drawLevel() {
		drawLevel(Collections.emptyList());
	}

	void drawLevel(List<Point> delta) {
		game.level().ifPresent(level -> display.atomically(() -> {
			int baseX = display.width() / 2 - level.width() + 1;
			int baseY = (display.height() - 2) / 2 - level.height() / 2 + 1;

			levelCursorX = levelCursorY = -1;
			levelColor = Attribute.BlackBackground;
			display.setAttribute(Attribute.BlackBackground, Attribute.BlackColor, Attribute.Bold);

			if (delta.isEmpty()) {
				for (int y = 0; y < level.height(); y++) {
					for (int x = 0; x < level.width(); x++) {
						drawLevelCell(level, x, y, baseX, baseY);
					}
				}
			} else {
				delta.forEach(pt -> drawLevelCell(level, pt.getX(), pt.getY(), baseX, baseY));
			}
		}));
	}

	private void drawLevelCell(Level level, int x, int y, int baseX, int baseY) {
		int drawX = baseX + x * 2;
		int drawY = baseY + y;

		if (levelCursorX != drawX || levelCursorY != drawY) {
			display.setCursor(drawY, drawX);
		}

		Level.Cell cell = level.cell(x, y);
		String chrs = "  ";
		Attribute color = cell.isTarget() ? Attribute.YellowBackground : Attribute.WhiteBackground;

		switch (cell.getContent()) {
			case Void:
				if (cell.isOutside()) color = Attribute.DefaultBackground;
				break;

			case Wall:
				color = Attribute.BlueBackground;
				break;

			case Player:
				color = Attribute.MagentaBackground;
				break;

			case Crate:
				if (cell.isTarget()) color = Attribute.GreenBackground;
				chrs = "[]";
				break;
		}

		if (color != levelColor) {
			display.setAttribute(color);
			levelColor = color;
		}

		display.write(chrs);

		levelCursorX = drawX + 2;
		levelCursorY = drawY;
	}

	private void drawStatus() {
		display.atomically(() -> {
			int status_row = display.height() - 1;
			int status_width = display.width() - 4;

			// Clear the last lines of the screen
			display.setCursor(status_row, 1);
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

	private void drawHistory() {
		display.atomically(() -> {
			int history_row = display.height() - 3;
			int history_width = display.width() - 4;

			String buffer = history;
			int dx = historyDx;
			if (buffer.length() > history_width) {
				if (-dx < history_width / 2) {
					buffer = "..." + buffer.substring(buffer.length() - history_width + 3);
				}
				else if (buffer.length() + dx < history_width / 2) {
					dx = -(history_width - (buffer.length() + dx));
					buffer = buffer.substring(0, history_width - 3) + "...";
				}
				else {
					int tmp = buffer.length() + dx - (history_width / 2 - 3);
					buffer = "..." + buffer.substring(tmp, tmp + history_width - 6) + "...";
					dx = -(history_width / 2);
				}
			}

			// Clear the lines of the screen
			display.setCursor(history_row, 1);
			display.setAttribute(Attribute.Reset);
			display.clearLine();
			display.setCursor(history_row + 1, 1);
			display.clearLine();

			// Draw history
			display.setAttribute(historyAttributes);
			display.setCursor(history_row, 2);
			display.write(' ');
			display.write(buffer);
			display.write(' ', history_width + 3 - (buffer.length() + 2));

			// history bar end
			display.setAttribute(Attribute.Reset);
			display.setCursor(history_row + 1, buffer.length() + dx + 3);
			display.write('^');
			display.clear(ClearLineDirection.RIGHT);
		});
	}

	void setStatusAttribtues(Attribute... attribtues) {
		statusAttributes = attribtues;
		drawStatus();
	}

	void setStatus(String text) {
		if (!text.equals(statusText)) {
			statusText = text;
			drawStatus();
		}
	}

	void setStatusRight(String text) {
		if (!text.equals(statusRightText)) {
			statusRightText = text;
			drawStatus();
		}
	}

	void setHistory(String text, int dx) {
		if (!text.equals(history) || historyDx != dx) {
			history = text;
			historyDx = dx;
			drawHistory();
		}
	}

	void sync() {
		display.atomically(() -> {
			display.setAttribute(Attribute.Reset);
			display.hideCursor();
			display.clear();
			drawLevel();
			drawHistory();
			drawStatus();
			display.flush();
		});
	}
}
