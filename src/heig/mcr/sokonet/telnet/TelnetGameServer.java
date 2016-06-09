package heig.mcr.sokonet.telnet;

import heig.mcr.sokonet.Display;
import heig.mcr.sokonet.Game;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.util.function.Function;

public class TelnetGameServer {
	private Function<Display, Game> factory;
	private int port;
	private Thread thread;

	public static TelnetGameServer bind(Function<Display, Game> factory, int port) {
		return new TelnetGameServer(factory, port);
	}

	private TelnetGameServer(Function<Display, Game> factory, int port) {
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
