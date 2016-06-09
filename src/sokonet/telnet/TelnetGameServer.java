package sokonet.telnet;

import sokonet.Game;
import sokonet.ansi.AnsiDisplay;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
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
			while (true) new Thread(new TelnetHandler(server.accept(), factory)).start();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
