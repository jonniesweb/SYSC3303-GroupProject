package serverLogic;

import entities.Player;

class User {
	String ip;
	int port;
	Player player;

	public User(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

}
