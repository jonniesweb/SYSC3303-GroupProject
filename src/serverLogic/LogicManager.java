package serverLogic;

import java.lang.String;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;

import entities.Door;
import entities.Entity;
import entities.Bomb;
import entities.Enemy;
import entities.Explosion;
import entities.Player;
import entities.Wall;
//import entities.Door;
//import entities.PowerUp;

import testing.Logger;
import gameLogic.GameBoard;
import Networking.Message;

// TODO: this class should manage the server GameBoard

/**
 * 
 * @author zachstoner
 *
 */
public class LogicManager implements Runnable {
	
	private BlockingQueue<Message> commandQueue = new LinkedBlockingQueue<Message>();
	
	private GameBoard board;
	
	private NetworkManager networkManager;
	private UserManager userManager;
	
	private Integer playerCount;
	
	private boolean gameInProgress;
	/**
	 * 
	 * @param uManager
	 * @param l
	 * @param nManager
	 */
	public LogicManager(UserManager uManager){
		// initialize board
		gameInProgress = false;
		this.board = new GameBoard(7,7);
		this.userManager = uManager;
		this.playerCount = uManager.getCurrentPlayerList().size();

	}
	
	public void setGameBoard(GameBoard board) {
		this.board = board;
	}
	
	/**
	 * 
	 * @param command
	 * @param playerID
	 */
	public void execute(Message m){
		try{
			commandQueue.put(m);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean safeMove(int x, int y){
		Entity entity = board.get(x,y);
		if(entity instanceof Enemy || entity instanceof Explosion || entity instanceof Player){
			if (entity instanceof Player){
				((Player) entity).loseLife();
			}
			return false;
		} else {return true;}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean validMove(int x, int y){
		Entity entity = board.get(x,y);
		if(entity instanceof Bomb || entity instanceof Wall || entity instanceof Door) { return false; }
		else { return true; }
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBoard(){
		return board.toString();
	}
	
	/**
	 * Places the currently playing players from gameManager to the GameBoard
	 */
	private static void placePlayers(GameBoard board, UserManager users) {

		// TODO: check bounds on placing player at x,y so it's not placing out of bounds
		
		int x;
		int y;
		System.out.println("LogicManager: Setting Player Positions");
		List<User> players = new ArrayList<User>();
		players.addAll(users.getCurrentPlayerList());
		for(int i= 0; i<players.size();i++){
			System.out.println(i);
				if(i==0){
					players.get(i).setPlayer(new Player(0,0,"Player 1"));
					board.set(players.get(i).getPlayer(),0,0);
					System.out.println(players.get(i).getPlayer().getName());
				}
				if(i == 1){
					players.get(i).setPlayer(new Player(6,6,"Player 2"));
					board.set(players.get(i).getPlayer(),6,6);
					System.out.println(players.get(i).getPlayer().getName());
				}
		}
		System.out.println("LogicManager: Current GameBoard\n" + board.toString());
	}
	
	/**
	 * 
	 * @return boolean gameInProgress
	 */
	public boolean getGameInProgress(){
		return gameInProgress;
	}
	
	public void setGameInProgress(boolean b){
		gameInProgress = b;
		placePlayers(board, userManager);
		System.out.println("LogicManager: gameInProgress has been set to '"+gameInProgress + "'");
	}
	public void setNetworkManager(NetworkManager m){
		this.networkManager = m;
		new Thread(this).start();
	}
	
	/**
	 * gets the currently playing player count
	 * @return
	 */
	public int getPlayerCount() {
		synchronized (playerCount) {
			return playerCount;
		}
	}
	
	/**
	 * increments the currently playing player count
	 */
	public void incrementPlayerCount() {
		synchronized (playerCount) {
			playerCount++;
		}
	}
	
	/**
	 * decrements the currently playing player count
	 */
	public void decrementPlayerCount() {
		synchronized (playerCount) {
			playerCount--;
		}
	}
	
	public void removePlayerFromGameBoard(Player player) {
		int x = player.getPosX();
		int y = player.getPosY();
		board.set(new Entity(x, y), x, y);
		
		System.out.println("LogicManager: " + player.getName() + " died");
	}
	
	/**
	 * 
	 * @param user
	 * @param newPosX
	 * @param newPosY
	 * @return
	 */
	private int handleMovement(User user, int newPosX, int newPosY){

		Player player = user.getPlayer();

		if(!safeMove(newPosX, newPosY)){
			player.loseLife();
			removePlayerFromGameBoard(player);
			System.out.println("LogicManager: Player '" + player.getName() + "' removed from board");
			return (-1);
		} else if (validMove(newPosX, newPosY)){
			board.remove(player.getPosX(), player.getPosY());
			player.setPos(newPosX, newPosY);
			board.set(player, newPosX, newPosY);
			System.out.println("LogicManager: Player '" + player.getName() + "' Moved Safely");
			return 1;
		} else if (board.hasDoor(newPosX, newPosY)){
			playerCount--;
			System.out.println("LogicManager: Player '" + player.getName() + "' Found the Door");
			return 2;
		}
		
		return 0;
	}

	/**
	 * 
	 * @param u
	 * @param command
	 * @return
	 */
	private int handleCommand(User u, String command){

		int posX = u.getPlayer().getPosX();
		int posY = u.getPlayer().getPosY();
		int playerStatus = 0;

		switch(command){
			case "UP":
				playerStatus = handleMovement(u, posX, posY - 1);
				break;
			case "DOWN":
				playerStatus = handleMovement(u, posX, posY + 1);
				break;
			case "LEFT":
				playerStatus = handleMovement(u, posX - 1, posY);
				break;
			case "RIGHT":
				playerStatus = handleMovement(u, posX + 1, posY);
				break;
			case "END_GAME":
				playerCount--;
				playerStatus = 3;
				break;
			default:
				System.out.println("LogicManager: '" + command + "' Unknown");
		}
		
		try{
			switch(playerStatus){
				case (-1)://Died
					if(!u.getPlayer().isAlive()){
						playerCount--;
						userManager.moveCurrentToFuture(u);
					}
					break;
				case 1://Moved Safely
					Logger.acceptMessage(u.getUUID() + " moved " + command);
					break;
				case 2://Found Door
					userManager.moveCurrentToFuture(u);
					break;
				case 3://End_Game Command Received
					//Same as Found Door
					//Added so that more functionality can be added to either
					// without affecting the other
					userManager.moveCurrentToFuture(u);
					break;
				default:
					System.out.println("LogicManager: Player Didn't Move");
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("LogicManager: Command '" + command + "' handled");
		return playerStatus;
	}

	public void run(){
		System.out.println("LogicManager: Now Active");

		Message m;
		String command;
		String uuid;

		System.out.println("LogicManager: Waiting on the Game to begin");

		while(!gameInProgress){
			//Don't do anything until the game has started
			Thread.yield();
		}

		System.out.println("LogicManager: The Games have begun!");

		try{

			outerLoop:
			
			while(playerCount > 0){
			
				System.out.println("LogicManager: Attempting to read a command.");
				//Block until a command message exists
				m = commandQueue.take();
				System.out.println("LogicManager: Command has been recieved.");
				
				command = new String(m.datagram.getData()).trim();
				uuid = m.datagram.getAddress().toString() + m.datagram.getPort();
				System.out.println("LogicManager: Recieved '" + command + "' from " + uuid);
				
				Object[] users = userManager.getCurrentPlayerList().toArray();
				for(int i = 0; i < users.length; i++){
					User u = (User)users[i];
					
					//Is this the User?
					if(u.getUUID().equals(uuid)){
						System.out.println("LogicManager: Manipulating player '" + u.getPlayer().getName() + "' currently at location " + u.getPlayer().getPos());

						// Proper way to do handle command
						//handleCommand(u, command);
						
						//The following is to preserve the debugging
						// functionality which ends the game
						// after a single player finds the door
						if (handleCommand(u, command) == 2){
							playerCount = 0;
							break outerLoop;
						}
					}
				}
				
				networkManager.sendBoardToAllClients(board.toString());
				Logger.acceptMessage("Board sent to all clients\n" + board.toString());
			}
			
			System.out.println("Logic Manager: Game has finished");
			networkManager.sendEndGameToAllClients();
			Logger.writeLog();
			Logger.endLog();
		} catch (Exception e){
			e.printStackTrace();
		}

		System.out.println("Logic Manager: Thread Finished Running");
	}
}
