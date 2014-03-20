package serverLogic;

import java.io.IOException;
import java.lang.String;
import java.util.concurrent.Semaphore;
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

//import testing.Logger;
import gameLogic.GameBoard;
import Networking.*;

import org.apache.log4j.*;


/**
 * 
 * @author zachstoner
 *
 */
public class LogicManager implements Runnable {
	
	private BlockingQueue<Message> commandQueue = new LinkedBlockingQueue<Message>();
	
	private GameBoard board;
	private BombFactory bombFactory;
	
	private NetworkManager networkManager;
	private UserManager userManager;
	
	private Integer playerCount;
	
	private int testMode = 0;

	
	public Semaphore testSem = null;
	
	private boolean gameInProgress;
	
	private static final Logger LOG = Logger.getLogger(LogicManager.class.getName());
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
		//log = Logger.getLogger(
	    //        LogicManager.class.getName());
	    //log = Logger.getLogger("Global");

	}
	
	public LogicManager(UserManager uManager, Semaphore s, int tMode){
		this(uManager);
		testSem = s;
		testMode = tMode;
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
		if(checkExplosion(x,y)){return false;}
		if(entity instanceof Enemy || entity instanceof Explosion || entity instanceof Player){
			return false;
		} else {return true;}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean validMove(int x, int y){
		Entity entity = board.get(x,y);
		if(checkBomb(x,y)){return false;}
		if(entity instanceof Bomb || entity instanceof Wall || entity instanceof Door) { return false; }
		else { return true; }
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBoard(){
		return board.toString(bombFactory.returnBombs(),bombFactory.returnExplosions());
	}
	
	/**
	 * Places the currently playing players from gameManager to the GameBoard
	 */
	private static void placePlayers(GameBoard board, UserManager users) {

		// TODO: check bounds on placing player at x,y so it's not placing out of bounds
		
		int x;
		int y;
		LOG.info("LogicManager: Setting Player Positions");
		List<User> players = new ArrayList<User>();
		players.addAll(users.getCurrentPlayerList());
		//set all player on board
		for(int i= 0; i<players.size();i++){
				if(i==0){
					players.get(i).setPlayer(new Player(0,0,"Player 1"));
					board.set(players.get(i).getPlayer());
					LOG.info(players.get(i).getPlayer().getName() + " SET ON BOARD");
				}
				if(i == 1){
					players.get(i).setPlayer(new Player(6,6,"Player 2"));
					board.set(players.get(i).getPlayer());
					LOG.info(players.get(i).getPlayer().getName() + " SET ON BOARD");
				}
		}
		LOG.info("LogicManager: Current GameBoard\n" + board.toString() );
	}
	public void placeEnemy(ArrayList<Enemy> eList){
		for(Enemy e : eList){
			board.set(e);
		}
	}
	public void setEnemy(Enemy e){
		board.set(e);
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
		bombFactory = new BombFactory(userManager.getCurrentPlayerList().toArray(),board.getWidth(),board.getHeight(),this);
		LOG.info("Game in progress has been set to '"+gameInProgress + "'");
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
		board.set(new Entity(x, y));
		LOG.info(player.getName() + " DIED");
	}
	public void removeEnemyFromGameBoard(Enemy enemy){
		board.remove(enemy.getPosX(), enemy.getPosY());
	}
	public void enemyValidMove(){
		
	}
	
	/**

	 * Handles Movement of the Player
	 * @param user
	 * @param newPosX
	 * @param newPosY
	 * @return playerStatus
	 */
	private int handleMovement(User user, int newPosX, int newPosY){

		Player player = user.getPlayer();

		if(!safeMove(newPosX, newPosY)){
			player.loseLife();
			return (-1);
		} else if (validMove(newPosX, newPosY)){
			board.remove(player.getPosX(), player.getPosY());
			player.setPos(newPosX, newPosY);
			board.set(player);
			//System.out.println("LogicManager: Player '" + player.getName() + "' Moved Safely");
			LOG.info(player.getName() + "' Moved Safely");
			LOG.info("BOARD VIEW\n" + board.toString(bombFactory.returnBombs(),bombFactory.returnExplosions()));
			LOG.info(player.getName() + " NEW LOCATION : " + player.getPos());
			return 1;
		} else if (board.hasDoor(newPosX, newPosY)){
			playerCount--;
			//System.out.println("LogicManager: Player '" + player.getName() + "' Found the Door");
			LOG.info(player.getName() + "' found the Door");
			return 2;
		}
		
		return 0;
	}
	
	// check to see if an explosion has gone off
	// at a players location
	private void checkBurnedPlayers(){
		User[] users = userManager.getCurrentPlayerList().toArray(new User[0]);
		Explosion[] explosions = bombFactory.returnExplosions();
		for(int i= 0; i < users.length; i++){
			Player p = users[i].getPlayer();
			for(int x =0; x < explosions.length; x++){
				if(p.getPosX() == explosions[x].getPosX() && p.getPosY()==explosions[x].getPosY()){
					p.loseLife();
					if(!p.isAlive()){
						removePlayerFromGameBoard(p);
						playerCount--;
					}
				}
			}
			
		}
	}
	// check to see if there is an explosion 
	// where the player is moving to
	private boolean checkExplosion(int x,int y){
		Explosion[] explosions = bombFactory.returnExplosions();
		for(int i = 0; i< explosions.length; i ++){
			if(explosions[i].getPosX()== x && explosions[i].getPosY() == y)
				return true;
		}
		return false;
	}
	// check if there is a bomb where the
	// player is moving to
	private boolean checkBomb(int x, int y){
		Bomb[] bombs = bombFactory.returnBombs();
		for(int i=0; i< bombs.length; i++){
			if(bombs[i].getPosX() == x && bombs[i].getPosY() ==y){
				return true;
			}
		}
		return false;
	}
	/**
	 * Executes the Users Commands
	 * @param u
	 * @param command
	 * @return playerStatus
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
			//case "BOMB" :
				
			case "END_GAME":
				playerCount--;
				playerStatus = 3;
				break;
			case "BOMB":
				bombFactory.startBomb(u);
				playerStatus = 0;
			default:
				System.out.println("LogicManager: '" + command + "' Unknown");
				LOG.error("COMMAND : " + command + " UNKNOWN");
		}
		
		try{
			switch(playerStatus){
				case (-1)://Died
					if(!u.getPlayer().isAlive()){
						playerCount--;
						removePlayerFromGameBoard(u.getPlayer());
						LOG.info(u.getPlayer().getName() + " removed from board");
						userManager.moveCurrentToFuture(u);
						if(testMode==1)
							testSem.release();
					}
					break;
				case 1://Moved Safely
					//Logger.acceptMessage(u.getUUID() + " moved " + command);
					break;
				case 2://Found Door
					userManager.moveCurrentToFuture(u);
					if(testMode==2)
						testSem.release();
					break;
				case 3://End_Game Command Received
					//Same as Found Door
					//Added so that more functionality can be added to either
					// without affecting the other
					userManager.moveCurrentToFuture(u);
					if(testMode==3)
						testSem.release();
					break;
				default:
					//System.out.println("LogicManager: Player Didn't Move");
					LOG.info("Player Didn't Move");
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		//System.out.println("LogicManager: Command '" + command + "' handled");
		LOG.info("Command '" + command + "' handled");
		return playerStatus;
	}


	private boolean handleBombCommand(String command){
		if(command.equals("EXP_ADDED")){
			checkBurnedPlayers();
			if(playerCount > 0)
				return false;
		}
			return true;
	}

	public void run(){

		//initialing variable
		LOG.info("LOGIC MANAGER STARTED...");
		UserMessage m;
		Message mes;
		BombMessage bm;
		String command;
		String uuid;
		Player p;
		
		//check if there is game still in progress
		LOG.info("WAITING FOR GAME TO START");
		while(!gameInProgress){
			//Don't do anything until the game has started
			Thread.yield();
		}

		LOG.info("GAME IS NOW IN PROGRESS");
		try{

			outerLoop:
			
			while(playerCount > 0){
				LOG.info("Attempting to read command ...");
				//reading command from queue
				mes = commandQueue.take();
				if(mes instanceof UserMessage){
					m = (UserMessage)mes;
					LOG.info("Command accepted");
				
					command = new String(m.datagram.getData()).trim();
					uuid = m.datagram.getAddress().toString() + m.datagram.getPort();
					Object[] users = userManager.getCurrentPlayerList().toArray();
					for(int i = 0; i < users.length; i++){
						User u = (User)users[i];
					
					//Is this the User?
						if(u.getUUID().equals(uuid)){
						//System.out.println(u.getPlayer().getName() + " CURRENT LOCATION : " + u.getPlayer().getPos());
							LOG.info(u.getPlayer().getName() + " CURRENT LOCATION : " + u.getPlayer().getPos());
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
				}else{
					bm = (BombMessage)mes;
					if(!handleBombCommand(bm.message))
						break outerLoop;
					
				}
				
				networkManager.sendBoardToAllClients(board.toString(bombFactory.returnBombs(),bombFactory.returnExplosions()));

				//Logger.acceptMessage("Board sent to all clients\n" + board.toString());
			}
			System.out.println("Logic Manager: Game has finished");
			networkManager.sendEndGameToAllClients();
			//Logger.writeLog();
			//Logger.endLog();
		} catch (Exception e){
			e.printStackTrace();
		}

		//System.out.println("Logic Manager: Thread Finished Running");

	}
}
