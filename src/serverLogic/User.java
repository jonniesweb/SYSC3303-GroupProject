package serverLogic;

import entities.Player;

/**
 * Represents a user's network connection and Player Entity.
 * 
 * The UUID should be ip+port. The purpose of the UUID is to differentiate
 * multiple users on the same computer. Setting the uuid to just the IP address
 * could create a conflict if two users were on the same IP address.
 * 
 */
public class User {
	private String ip;
	private int port;
	private Player player;
	private String uuid;

	public User(String uuid, String ip, int port) {
		this.uuid = uuid;
		this.ip = ip;
		this.port = port;
		this.setPlayer(new Player(-1,-1,ip));
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getUUID() {
		return uuid;
	}

}