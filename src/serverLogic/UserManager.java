package serverLogic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserManager {

	private Map<String, User> currentPlayerList, futurePlayerList,
			spectatorList;

	public static int MAX_PLAYERCOUNT = 2;

	public UserManager() {
		// pass a default # of players per game
		this(2);

	}
	
	public UserManager(int numberOfPlayers) {
		// define maps that track all users connected to server
		currentPlayerList = Collections.synchronizedMap(new HashMap<String, User>(numberOfPlayers));
		futurePlayerList = Collections.synchronizedMap(new HashMap<String, User>(numberOfPlayers));
		spectatorList = Collections.synchronizedMap(new HashMap<String, User>(numberOfPlayers));
	}

	public enum Type {
		PLAYER, SPECTATOR
	}
	
	/**
	 * Adds a user to the currentPlayerList, automatically giving it a uuid of
	 * <code>ip+port</code>
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	public void addPlayerToCurrent(String ip, int port)
			throws OverwroteUserInMapException {
		addPlayerToCurrent(ip + port, ip, port);
	}

	/**
	 * Adds a user to the currentPlayerList
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	private void addPlayerToCurrent(String uuid, String ip, int port)
			throws OverwroteUserInMapException {
		if (currentPlayerList.size() == MAX_PLAYERCOUNT) {
			addPlayerToFuture(uuid, ip, port);
		}

		if (currentPlayerList.put(uuid, new User(uuid, ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}

	/**
	 * Adds a user to the futurePlayerList, automatically giving it a uuid of
	 * <code>ip+port</code>
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	public void addPlayerToFuture(String ip, int port)
			throws OverwroteUserInMapException {
		addPlayerToFuture(ip + port, ip, port);
	}

	/**
	 * Adds a user to the futurePlayerList list ??? Possibly add player to
	 * playerList also????
	 * 
	 * @param uuid
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	private void addPlayerToFuture(String uuid, String ip, int port)
			throws OverwroteUserInMapException {
		if (futurePlayerList.put(uuid, new User(uuid, ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}
	
	public void addSpectator(String ip, int port) throws OverwroteUserInMapException {
		addSpectator(ip+port, ip, port);
	}

	/**
	 * Adds a user to the spectatorList list
	 * 
	 * @param ip
	 * @param port
	 * @throws OverwroteUserInMapException
	 */
	private void addSpectator(String uuid, String ip, int port)
			throws OverwroteUserInMapException {
		if (spectatorList.put(uuid, new User(uuid, ip, port)) != null) {
			throw new OverwroteUserInMapException();
		}
	}
	
	/**
	 * Removes the user from the system.
	 * @param user
	 */
	public void removeUser(User user) {
		String uuid = user.getUUID();
		if (currentPlayerList.containsKey(uuid)) {
			currentPlayerList.remove(uuid);
		}
		if (futurePlayerList.containsKey(uuid)) {
			futurePlayerList.remove(uuid);
		}
		if (spectatorList.containsKey(uuid)) {
			spectatorList.remove(uuid);
		}
		if (currentPlayerList.containsKey(uuid)) {
			currentPlayerList.remove(uuid);
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
		if (currentPlayerList.containsKey(uuid)) {
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
		moveFutureToCurrent(user.getUUID());
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
		moveCurrentToFuture(user.getUUID());
	}
	
	/**
	 * Gets all users that are currently playing the game. ie. users that are
	 * not dead.
	 * 
	 * @return
	 */
	public Collection<User> getCurrentPlayerList() {
		return currentPlayerList.values();
	}

	/**
	 * Gets all users that are waiting to be in the currentPlayerList because
	 * they are dead, they joined when a game was already in progress, or
	 * MAX_PLAYERCOUNT was maxxed.
	 * 
	 * @return
	 */
	public Collection<User> getFuturePlayerList() {
		return futurePlayerList.values();
	}

	/**
	 * Gets all users that are spectators. Spectators are users that don't send
	 * input except for joining and leaving.
	 * 
	 * @return
	 */
	public Collection<User> getSpectatorList() {
		return spectatorList.values();
	}

	/**
	 * Returns all users connected to the server in a list
	 * 
	 * @return
	 */
	public List<User> getAllUsers() {
		List<User> all = new LinkedList<User>();
		all.addAll(getCurrentPlayerList());
		all.addAll(getFuturePlayerList());
		all.addAll(getSpectatorList());
		return all;
	}

	/**
	 * A user with the same uuid was overwritten. This could have been called if
	 * a uuid key/value was replaced with another value.
	 */
	class OverwroteUserInMapException extends Exception {
	}

	/**
	 * Attempting to get a user with the specified uuid throws this error if the
	 * uuid isn't found in the respective map.
	 */
	class NoUserExistsWithUUIDException extends Exception {
	}
}
