package serverLogic;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import entities.Player;

// TODO: hold each player/spectator's ip and port
// TODO: add getters and methods that move players to other lists
// TODO: when adding player to current hashmap add them to playerList (used by logicManager)
//TODO return all users so i can send them gameboard
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

	public List<Player> getCurrentPlayerList() {
		return playerList;
	}

	/**
	 * Adds a user to the currentPlayerList list
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	public void addPlayerToCurrent(String uuid, String ip, int port)
			throws OverwroteUserInMapException {
		if (currentPlayerList.put(uuid, new User(uuid, ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}

	/**
	 * Adds a user to the futurePlayerList list ??? Possibly add player to
	 * playerList also????
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	public void addPlayerToFuture(String uuid, String ip, int port)
			throws OverwroteUserInMapException {
		if (futurePlayerList.put(uuid, new User(uuid, ip, port)) != null) {
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
	public void addSpectator(String uuid, String ip, int port)
			throws OverwroteUserInMapException {
		if (spectatorList.put(uuid, new User(uuid, ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}

	/**
	 * Moves the player with the specified uuid from the futurePlayerList to
	 * currentPlayerList.
	 * 
	 * @param uuid
	 * @throws OverwroteUserInMapException
	 * @throws NoUserExistsWithUUIDException
	 */
	public void moveFutureToCurrent(String uuid)
			throws OverwroteUserInMapException, NoUserExistsWithUUIDException {
		if (futurePlayerList.containsKey(uuid)) {
			if (currentPlayerList.put(uuid, futurePlayerList.remove(uuid)) == null) {
				return; // success!
			} else {
				throw new OverwroteUserInMapException();
			}
		} else {
			throw new NoUserExistsWithUUIDException();
		}
	}

	/**
	 * Moves the player with the specified uuid from the currentPlayerList to
	 * futurePlayerList.
	 * 
	 * @param uuid
	 * @throws OverwroteUserInMapException
	 * @throws NoUserExistsWithUUIDException
	 */
	public void moveCurrentToFuture(String uuid)
			throws OverwroteUserInMapException, NoUserExistsWithUUIDException {
		if (futurePlayerList.containsKey(uuid)) {
			if (futurePlayerList.put(uuid, currentPlayerList.remove(uuid)) == null) {
				return; // success!
			} else {
				throw new OverwroteUserInMapException();
			}
		} else {
			throw new NoUserExistsWithUUIDException();
		}
	}

	/**
	 * Moves the player with the specified uuid from the futurePlayerList to
	 * currentPlayerList.
	 * 
	 * @param user
	 * @throws OverwroteUserInMapException
	 * @throws NoUserExistsWithUUIDException
	 */
	public void moveFutureToCurrent(User user)
			throws OverwroteUserInMapException, NoUserExistsWithUUIDException {
		moveFutureToCurrent(user.uuid);
	}

	/**
	 * Moves the player with the specified uuid from the currentPlayerList to
	 * futurePlayerList.
	 * 
	 * @param user
	 * @throws OverwroteUserInMapException
	 * @throws NoUserExistsWithUUIDException
	 */
	public void moveCurrentToFuture(User user)
			throws OverwroteUserInMapException, NoUserExistsWithUUIDException {
		moveCurrentToFuture(user.uuid);
	}

	// TODO return all users so i can send them gameboard
	/**
	 * @deprecated use the other getter methods below since you can get
	 *             currentPlayers, futurePlayers or spectators
	 * @return
	 */
	public List<User> getAllUsers() {
		return null;
		// return all users so i can send them the game board
	}
	
	public List<User> getCurrentPlayerList() {
		return currentPlayerList.values();
	}
	
	public List<User> getFuturePlayerList() {
		return futurePlayerList.values();
	}
	
	public List<User> getSpectatorList() {
		return spectatorList.values();
	}
	
	public List<User> getAllUsers() {
		
	}


	class OverwroteUserInMapException extends Exception {
	}

	class NoUserExistsWithUUIDException extends Exception {

	}
}
