package serverLogic;

import entities.Player;

class User {
	String uuid;
	String ip;
	int port;
	Player player;

	public User(String uuid, String ip, int port) {
		this.uuid = uuid;
		this.ip = ip;
		this.port = port;
	}

}