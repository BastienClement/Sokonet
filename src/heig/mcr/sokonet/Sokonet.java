package heig.mcr.sokonet;

import heig.mcr.sokonet.game.Sokoban;
import heig.mcr.sokonet.telnet.TelnetGameServer;

public class Sokonet {
	public static void main(String... args) {
		TelnetGameServer.bind(Sokoban::new, 2121);
	}
}
