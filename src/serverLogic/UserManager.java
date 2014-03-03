package serverLogic;

import java.util.HashMap;

import entities.Player;

// TODO: hold each player/spectator's ip and port
// TODO: add getters and methods that move players to other lists
public class UserManager {

	HashMap<String, User> currentPlayerList, futurePlayerList, spectatorList;

	public UserManager() {

		final int initialMapSize = 2;

		currentPlayerList = new HashMap<String, User>(initialMapSize);
		futurePlayerList = new HashMap<String, User>(initialMapSize);
		spectatorList = new HashMap<String, User>(initialMapSize);
	}

	public enum Type {
		PLAYER, SPECTATOR
	};

	/**
	 * Adds a user to the currentPlayerList list
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	public void addPlayerToCurrent(String ip, int port)
			throws OverwroteUserInMapException {
		if (currentPlayerList.put(ip, new User(ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}

	/**
	 * Adds a user to the futurePlayerList list
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	public void addPlayerToFuture(String ip, int port)
			throws OverwroteUserInMapException {
		if (futurePlayerList.put(ip, new User(ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}

	/**
	 * Adds a user to the spectatorList list
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	public void addSpectator(String ip, int port)
			throws OverwroteUserInMapException {
		if (spectatorList.put(ip, new User(ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}

	/**
	 * Represents a user's network connection and Player Entity, if required.
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

	class OverwroteUserInMapException extends Exception {
	}
}
