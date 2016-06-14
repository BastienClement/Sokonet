package sokonet;

import sokonet.game.LevelFactory;
import sokonet.game.Sokoban;
import sokonet.telnet.TelnetGameServer;

import java.io.IOException;

/**
 * Main class.
 */
public class Sokonet {
	public static void main(String... args) throws IOException {
		LevelFactory.load("./levels.txt");
		TelnetGameServer.bind(Sokoban::new, 2121).start();
	}
}
