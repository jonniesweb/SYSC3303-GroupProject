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
import entities.PowerUp;
import entities.Wall;
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
	private Integer currentPlayers;
	
	private int testMode = 0;
	
	private EnemyManager enemies;

	
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
		this.currentPlayers = this.playerCount;

	}
	/**
	 * For Testing Purposes
	 * @param uManager
	 * @param s
	 * @param tMode
	 */
	
	public LogicManager(UserManager uManager, Semaphore s, int tMode){
		this(uManager);
		testSem = s;
		testMode = tMode;
	}
	/**
	 * Set board to logic manager
	 * @param board
	 */
	public void setGameBoard(GameBoard board) {
		this.board = board;
	}
	
	public GameBoard getGameBoard(){
		return this.board;
	}
	
	/**
	 * Put message into queue
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
	 * Check whether the next command player will make is a safemove or not.
	 * e.g If next movement touch enemy,player lose life
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean safeMove(int x, int y) {
		Entity entity = board.get(x, y);
		if (checkExplosion(x, y)) {
			LOG.info("Player died from explosion");
			return false;
		}
		if (enemies.checkEnemy(x, y)) {
			LOG.info("Player died from enemy");
			return false;
		}
		if (entity instanceof Enemy || entity instanceof Explosion
				|| entity instanceof Player) {
			LOG.info("Player died from " + entity.toString());
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Check next movement of player is valid or not
	 * e.g No Wall on next movement
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean validMove(int x, int y){
		if( (x < 0) || (x > board.getWidth()) || (y < 0) || (y>board.getHeight())) return false;
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
		return board.toString(bombFactory.returnBombs(),bombFactory.returnExplosions(),enemies.returnEnemies());
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
				if(i == 2){
					players.get(i).setPlayer(new Player(6,6,"Player 2"));
					board.set(players.get(i).getPlayer());
					LOG.info(players.get(i).getPlayer().getName() + " SET ON BOARD");
				}
		}
		LOG.info("LogicManager: Current GameBoard\n" + board.toString() );
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
		if(b){
			
			board.randomizeFloor(userManager.getCurrentPlayerList().size());
			placePlayers(board, userManager);
			this.playerCount = this.userManager.getCurrentPlayerList().size();
			this.currentPlayers = this.playerCount;
			enemies = new EnemyManager(this);
			bombFactory = new BombFactory(userManager.getCurrentPlayerList().toArray(),board.getWidth(),board.getHeight(),this);
			board.set(new PowerUp(6, 5));

			if(testMode == 0) new Thread(enemies).start();
			//Set a static enemy for when testing for lose scenarios
			//DO NOT CHANGE ENEMY(2,3) TestLose REVOLVES AROUND AN ENEMY AT POSITION (2,3)
			else if(testMode == 1) board.set(new Enemy(2,3));
			else ;
			
			networkManager.sendBoardToAllClients(getBoard());

		}
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
	
	public void removePlayerFromGameBoard(User player) {
		int x = player.getPlayer().getPosX();
		int y = player.getPlayer().getPosY();
		board.set(new Entity(x, y));
		LOG.info(player.getPlayer().getName() + " DIED");
		try{
		userManager.moveCurrentToFuture(player.getUUID());
		}catch(Exception e){e.printStackTrace();}
		
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
			
			// check for powerup before moving to that location
			if (board.hasPowerUp(newPosX, newPosY)) {
				LOG.info(player.getName() + " got PowerUp at " + player.getPos());
				player.setBombRangePowerUpEnabled(true);
				
				// move player, same as below. Should probably refactor
				board.remove(player.getPosX(), player.getPosY());
				player.setPos(newPosX, newPosY);
				board.set(player);
				LOG.info(player.getName() + "' Moved Safely");
				LOG.info("BOARD VIEW\n" + board.toString(bombFactory.returnBombs(),bombFactory.returnExplosions(),enemies.returnEnemies()));
				LOG.info(player.getName() + " NEW LOCATION : " + player.getPos());
				
				return 4;
			}
			
			board.remove(player.getPosX(), player.getPosY());
			player.setPos(newPosX, newPosY);
			board.set(player);
			LOG.info(player.getName() + "' Moved Safely");
			LOG.info("BOARD VIEW\n" + board.toString(bombFactory.returnBombs(),bombFactory.returnExplosions(),enemies.returnEnemies()));
			LOG.info(player.getName() + " NEW LOCATION : " + player.getPos());
			return 1;
		} else if (board.hasDoor(newPosX, newPosY)){
			currentPlayers--;
			LOG.info(player.getName() + "' found the Door");
			return 2;
		}
		
		return 0;
	}
	
	// check to see if an explosion has gone off
	// at a players location
	private void checkBurnedEntities(){
		User[] users = userManager.getCurrentPlayerList().toArray(new User[0]);
		Explosion[] explosions = bombFactory.returnExplosions();
		LOG.info("There are:"+explosions.length+" many explosions");
		for(int i= 0; i < users.length; i++){
			Player p = users[i].getPlayer();
			for(int x =0; x < explosions.length; x++){
				if(p.getPosX() == explosions[x].getPosX() && p.getPosY()==explosions[x].getPosY()){
					LOG.info("explosion x y is:"+ p.getPosX() +p.getPosY());
					p.loseLife();
					if(!p.isAlive()){
						removePlayerFromGameBoard(users[i]);
						LOG.info("player died from bomb blast");
						playerCount--;
						currentPlayers--;
					}
				}
			}	
		}
		for(int i = 0; i< explosions.length; i++)
			this.destroy(explosions[i].getPosX(), explosions[i].getPosY());
	}
	// check to see if there is an explosion 
	// where the player is moving to
	public boolean checkExplosion(int x,int y){
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
				
			case "END_GAME":
				playerCount--;
				currentPlayers--;
				playerStatus = 3;
				break;
				
			case "BOMB":
				bombFactory.startBomb(u);
				playerStatus = 0;
				break;
				
			default:
				System.out.println("LogicManager: '" + command + "' Unknown");
				LOG.error("COMMAND : " + command + " UNKNOWN");
		}
		
		try{
			switch(playerStatus){
				case (-1)://Died
					if(!u.getPlayer().isAlive()){
						playerCount--;
						currentPlayers--;
						removePlayerFromGameBoard(u);
						LOG.info(u.getPlayer().getName() + " removed from board");
						
						
						if(testMode == 1){
						
							testSem.release();
						}
					}
					break;
				case 1://Moved Safely
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
					
					if(testMode==3)
						testSem.release();
					break;
				case 4: // player picked up powerup
					break;
				default:
					LOG.info("Player Didn't Move");
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		LOG.info("Command '" + command + "' handled");
		return playerStatus;
	}


	private boolean handleBombCommand(String command){
		if(command.equals("EXP_ADDED")){
			LOG.info("playerCount before burn check: "+playerCount);
			checkBurnedEntities();
			if(playerCount < 0)
				return true;
		}
			LOG.info("bomb command should say game is not over");
			return false;
	}
	private void destroy(int x,int y){
		if(board.get(x, y) instanceof Wall ){
			LOG.info("REMOVED WALL ON (" + x + "," + y + ")");
			board.remove(x, y);
		}
		else if(enemies.removeEnemy(x, y)){
			LOG.info("REMOVED ENEMY ON (" + x + "," + y + ")");
			board.remove(x, y);
			enemies.removeEnemy(x, y);
		}
	}
	
	public void shutdown(){
		System.out.println("==========LogicManager shutdown");
		playerCount = 0;
		gameInProgress = false;
		//networkManager.shutdown();
	}
	
	private void newFloor(){
		if(testMode != 0){
			playerCount = 0;
			return;
		}
		
		board = new GameBoard(board.getWidth(),board.getHeight());
		
		for(User u: userManager.getFuturePlayerList()){
			try{
				userManager.moveFutureToCurrent(u);
			} catch (Exception e){
				//kill the game
				playerCount = 0;
				e.printStackTrace();
			}
		}
		
		setGameInProgress(true);
		
		LOG.info("NEW FLOOR STARTING");
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
		playerCount = userManager.getCurrentPlayerList().size();
		while(!gameInProgress){
			//Don't do anything until the game has started
			Thread.yield();
		}

		LOG.info("GAME IS NOW IN PROGRESS");
		try{

			outerLoop:
			
			while(playerCount > 0){
				
				if(currentPlayers == 0){
					newFloor();
				}
				LOG.info("PLAYER COUNT IS: "+playerCount);
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
						handleCommand(u, command);
						
						//The following is to preserve the debugging
						// functionality which ends the game
						// after a single player finds the door

						/*	if (handleCommand(u, command) == 2){
								playerCount = 0;
								break outerLoop;
							}*/
						}

					}
				}else{
					bm = (BombMessage)mes;
					LOG.info("got bomb command");
					if(handleBombCommand(bm.message)){
						LOG.info("bomb command said end game");
						break outerLoop;
					}
					
				}
				
				networkManager.sendBoardToAllClients(board.toString(bombFactory.returnBombs(),bombFactory.returnExplosions(),
																	enemies.returnEnemies()));
			}
		
			LOG.info("GAME ENDED!");
			networkManager.sendEndGameToAllClients();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		System.out.println("=======logic manager done done done=======");
	}
	
	void sendGameBoardToAll() {
		networkManager.sendBoardToAllClients(getBoard());
		LOG.info("BOARD VIEW\n" + getBoard());
	}
}
