package serverLogic;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import entities.Player;

// TODO: hold each player/spectator's ip and port
// TODO: add getters and methods that move players to other lists
// TODO: when adding player to current hashmap add them to playerList (used by logicManager)
public class UserManager {

	HashMap<String, User> currentPlayerList, futurePlayerList, spectatorList;
	// needed by logic Manager to iterate over all current players
	private List<Player> playerList;

	public UserManager() {

		final int initialMapSize = 2;
		playerList = Collections.synchronizedList(new LinkedList<Player>());
		currentPlayerList = new HashMap<String, User>(initialMapSize);
		futurePlayerList = new HashMap<String, User>(initialMapSize);
		spectatorList = new HashMap<String, User>(initialMapSize);
	}

	public enum Type {
		PLAYER, SPECTATOR
	}

	public List<Player> getCurrentPlayerList(){
		return playerList;
	}
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
	 * ??? Possibly add player to playerList also????
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
	
	//TODO
	public List<User> getAllUsers(){
		
		//return all users so i can send them the game board
	}

	class OverwroteUserInMapException extends Exception {
	}
}
