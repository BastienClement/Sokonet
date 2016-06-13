package sokonet.telnet;

import sokonet.Game;
import sokonet.ansi.AnsiDisplay;
import sokonet.ansi.Attribute;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class TelnetGameServer {
	private Function<? super TelnetDisplay, Game> factory;
	private int port;
	private Thread thread;

	public static TelnetGameServer bind(Function<? super AnsiDisplay, Game> factory, int port) {
		return new TelnetGameServer(factory, port);
	}

	private TelnetGameServer(Function<? super TelnetDisplay, Game> factory, int port) {
		this.factory = factory;
		this.port = port;
		this.thread = new Thread(this::run);
	}

	public void start() {
		thread.start();
	}

	@SuppressWarnings("InfiniteLoopStatement")
	private void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket socket = server.accept();
				Thread thread = new Thread(new TelnetHandler(socket, factory));
				thread.setName(socket.getRemoteSocketAddress().toString());

				// Catch game exceptions
				thread.setUncaughtExceptionHandler((t, ex) -> {
					try {
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						ex.printStackTrace(new PrintStream(buffer));

						String trace = new String(buffer.toByteArray(), StandardCharsets.US_ASCII)
							.replace("\r\n", "\n")
							.replace("\n", "\r\n");

						TelnetDisplay display = new TelnetDisplay(socket.getOutputStream());
						display.setAttribute(Attribute.RedBackground, Attribute.WhiteColor, Attribute.Bold);
						display.clearLine();
						display.write("UNCAUGHT EXCEPTION");
						display.setAttribute(Attribute.Normal);
						display.write("\r\n\r\n");
						display.write(trace);
						display.setAttribute(Attribute.Reset);
						display.write("\r\n");
						display.flush();

						socket.close();
					} catch (IOException ignored) {}
				});

				thread.start();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
