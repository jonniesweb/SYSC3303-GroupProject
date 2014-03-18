package serverLogic;

import java.io.IOException;
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

//import testing.Logger;
import gameLogic.GameBoard;
import Networking.Message;

import org.apache.log4j.*;

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
	
	private static final Logger LOG = Logger.getLogger(
            LogicManager.class.getName());
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
		LOG.info("SETTING PLAYER");
		List<User> players = new ArrayList<User>();
		players.addAll(users.getCurrentPlayerList());
		//set all player on board
		for(int i= 0; i<players.size();i++){
				if(i==0){
					players.get(i).setPlayer(new Player(0,0,"Player 1"));
					board.set(players.get(i).getPlayer(),0,0);
					LOG.info(players.get(i).getPlayer().getName() + " SET ON BOARD");
				}
				if(i == 1){
					players.get(i).setPlayer(new Player(6,6,"Player 2"));
					board.set(players.get(i).getPlayer(),6,6);
					LOG.info(players.get(i).getPlayer().getName() + " SET ON BOARD");
				}
		}
		LOG.info("BOARD VIEW\n" + board.toString());
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
		LOG.info("Game in progress is="+gameInProgress);
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
		
		LOG.info(player.getName() + " DIED");
	}
	
	/**
	 * 
	 */
	public void run(){
		//initialing variable
		LOG.info("LOGIC MANAGER STARTED...");
		Message m;
		String command;
		String uuid;
		Player p;
		
		//check if there is game still in progress
		LOG.info("WAITING FOR GAME TO START");
		while(!gameInProgress){
			Thread.yield();
		}
		LOG.info("GAME IS NOW IN PROGRESS");
		try{
			
			outerLoop:
			
			while(playerCount > 0){
				LOG.info("Attempting to read command ...");
				//reading command from queue
				m = commandQueue.take();
				LOG.info("Command accepted");
				
				command = new String(m.datagram.getData()).trim();
				uuid = m.datagram.getAddress().toString() + m.datagram.getPort();
				
				Object[] users = userManager.getCurrentPlayerList().toArray();
				for(int i =0;i < users.length;i++){
					User u = (User)users[i];
					p = u.getPlayer();
					if(u.getUUID().equals(uuid)){
						LOG.info("MATCHING ID - " + p.getName());
						LOG.info("COMMAND " + command + " for " + p.getName());
						LOG.info(p.getName() + " CURRENT POSITION : (" + p.getPosX() + "," + p.getPosY()+")");
//==================================================================================						
//									UP
//==================================================================================						
						if(command.equals("UP")){
							if (!safeMove(p.getPosX(), p.getPosY() - 1)){
								//lose life when player makes an unsafe move
								p.loseLife();
								LOG.info(p.getName() + "LOSE A LIFE");
								//remove player
								removePlayerFromGameBoard(p);
								LOG.info(p.getName() + "REMOVED FROM BOARD");
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
									
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() - 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveUp();
									board.set(p,p.getPosX(),p.getPosY());
									LOG.info(p.getName() + " NEW POSITION : (" + p.getPosX() + "," + p.getPosY()+")");
							}
							else if(board.hasDoor(p.getPosX(), p.getPosY() - 1)){
								LOG.info(p.getName() + " REACHED THE DOOR");
								playerCount--;
								userManager.moveCurrentToFuture(u);
								break outerLoop;
							}
							LOG.info("BOARD VIEW\n" + board.toString());
								
						}
//==================================================================================						
//									DOWN
//==================================================================================
						else if(command.equals("DOWN")){
							if (!safeMove(p.getPosX(), p.getPosY() + 1)){
								//lose life when player makes an unsafe move
								p.loseLife();
								LOG.info(p.getName() + "LOSE A LIFE");
								//remove player
								removePlayerFromGameBoard(p);
								LOG.info(p.getName() + "REMOVED FROM BOARD");
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveDown();
									board.set(p,p.getPosX(),p.getPosY());
									LOG.info(p.getName() + " NEW POSITION : (" + p.getPosX() + "," + p.getPosY()+")");
							}
							else if(board.hasDoor(p.getPosX(), p.getPosY() + 1)){
								LOG.info(p.getName() + " REACHED THE DOOR");
								playerCount--;
								userManager.moveCurrentToFuture(u);
								break outerLoop;
							}
							LOG.info("BOARD VIEW\n" + board.toString());
						}
//==================================================================================						
//									LEFT
//==================================================================================
						else if(command.equals("LEFT")){
							if (!safeMove(p.getPosX()-1, p.getPosY())){
								//lose life when player makes an unsafe move
								p.loseLife();
								LOG.info(p.getName() + "LOSE A LIFE");
								//remove player
								removePlayerFromGameBoard(p);
								LOG.info(p.getName() + "REMOVED FROM BOARD");
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX()-1, p.getPosY())){
									board.remove(p.getPosX(), p.getPosY());
									p.moveLeft();
									board.set(p,p.getPosX(),p.getPosY());
									LOG.info(p.getName() + " NEW POSITION : (" + p.getPosX() + "," + p.getPosY()+")");
							}
							else if(board.hasDoor(p.getPosX()-1, p.getPosY())){
								LOG.info(p.getName() + " REACHED THE DOOR");
								playerCount--;
								userManager.moveCurrentToFuture(u);
								break outerLoop;
							}
							LOG.info("BOARD VIEW\n" + board.toString());
						}
//==================================================================================						
//									RIGHT
//==================================================================================
						else if(command.equals("RIGHT")){
							if (!safeMove(p.getPosX() + 1, p.getPosY())){
								//lose life when player makes an unsafe move
								p.loseLife();
								LOG.info(p.getName() + "LOSE A LIFE");
								//remove player
								removePlayerFromGameBoard(p);
								LOG.info(p.getName() + "REMOVED FROM BOARD");
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX()+1, p.getPosY())){
									board.remove(p.getPosX(), p.getPosY());
									p.moveRight();
									board.set(p,p.getPosX(),p.getPosY());
									LOG.info(p.getName() + " NEW POSITION : (" + p.getPosX() + "," + p.getPosY()+")");
							}
							else if(board.hasDoor(p.getPosX()+1, p.getPosY())){
								LOG.info(p.getName() + " REACHED THE DOOR");
								playerCount--;
								userManager.moveCurrentToFuture(u);
								break outerLoop;
							}
							LOG.info("BOARD VIEW\n" + board.toString());
						}
//=====================================================================================
//									END GAME
//=====================================================================================
						else if(command.equals("END_GAME")){
							LOG.info("GAME ENDED");
							playerCount--;
							userManager.moveCurrentToFuture(u);
						}
						//else if(command.equals("BOMB"))
					}
				}
				//board.update();
				networkManager.sendBoardToAllClients(board.toString());
				//Logger.acceptMessage("Board sent to all client");
				//Logger.acceptMessage(board.toString());
			}
			LOG.info("GAME IS OVER");
			networkManager.sendEndGameToAllClients();
			//Logger.writeLog();
			//Logger.endLog();
		}catch(Exception e){
			e.printStackTrace();
		}
		LOG.info("GAME IS OVER");
	}
}
