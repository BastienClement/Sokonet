package sokonet.telnet;

import sokonet.Game;
import sokonet.Key;
import sokonet.KeyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.function.Function;

import static sokonet.telnet.TelnetOpcode.*;

class TelnetHandler implements Runnable {
	private Socket socket;
	private Function<? super TelnetDisplay, Game> factory;

	private InputStream in;
	private OutputStream out;
	private TelnetDisplay display;
	private Game game;

	TelnetHandler(Socket socket, Function<? super TelnetDisplay, Game> factory) throws IOException {
		this.socket = socket;
		this.factory = factory;

		in = socket.getInputStream();
		out = socket.getOutputStream();
		display = new TelnetDisplay(out);
	}

	private void startGame() {
		if (game != null) throw new IllegalStateException();
		game = factory.apply(display);
	}

	@Override
	public void run() {
		try {
			command(WILL, ECHO);
			command(WILL, SUPPRESS_GO_AHEAD);
			command(WONT, LINEMODE);
			command(DO, WINDOW_SIZE);

			while (true) {
				int code = in.read();
				if (code < 0) break;

				// Control codes
				switch (code) {
					case 0xFF: // IAC
						readCommand();
						continue;
					case 3: // CTRL-C
						socket.close();
						return;
					case 13:
						// ASCII code 13 is always followed by code 0, ignore
						in.read();
				}

				Key key;
				boolean shift = false;
				boolean ctrl = false;

				if (code >= 1 && code <= 26) {
					key = Key.forCode(code + 96);
					ctrl = true;
				} else if (code >= 65 && code <= 90) {
					key = Key.forCode(code + 32);
					shift = true;
				} else {
					key = Key.forCode(code);
				}

				if (game == null) throw new IllegalStateException();
				game.keyPressed(new KeyEvent(key, code, shift, ctrl));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void command(TelnetOpcode... ops) throws IOException {
		out.write(IAC.code);
		for (TelnetOpcode o : ops) out.write(o.code);
		out.flush();
	}

	private void readCommand() throws IOException {
		int code = in.read();
		switch (TelnetOpcode.fromByte(code)) {
			case WONT:
				if (TelnetOpcode.fromByte(in.read()) == WINDOW_SIZE && game == null) startGame();
				break;

			case DO:
			case DONT:
			case WILL:
				// Ignore option replies
				in.read();
				break;

			case SB:
				int sub_code = in.read();
				switch (TelnetOpcode.fromByte(sub_code)) {
					case WINDOW_SIZE:
						int width = in.read() << 8 | in.read();
						int height = in.read() << 8 | in.read();
						display.setSize(width, height);
						in.read(); // IAC
						in.read(); // SE
						if (game == null) {
							startGame();
						} else {
							game.displaySizeChanged();
						}
						break;

					default:
						throw new UnsupportedOperationException("Unknown subnegotiation code: " + sub_code);
				}
				break;

			default:
				throw new UnsupportedOperationException("Unknown command code: " + code);
		}
	}
}
