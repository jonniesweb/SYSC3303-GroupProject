package serverLogic;

import entities.Player;

/**
 * Represents a user's network connection and Player Entity.
 * 
 * The uuid is a unique identifier that should try to differentiate one user
 * connection from the other. The reason for this is to try and solve the
 * possible issue of having two User instances with the exact same IP and
 * Port (or I'm wrong since that usage case can't exist).
 * 
 */
class User {
	String ip;
	int port;
	Player player;

	public User(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

}