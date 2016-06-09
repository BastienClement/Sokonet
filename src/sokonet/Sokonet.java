package sokonet;

import sokonet.game.Sokoban;
import sokonet.telnet.TelnetGameServer;

public class Sokonet {
	public static void main(String... args) {
		TelnetGameServer.bind(Sokoban::new, 2121).start();
	}
}
