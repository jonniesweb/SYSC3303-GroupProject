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
		System.out.println("setting players");
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
		System.out.println(board.toString());
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
		System.out.println("Game in progress is: "+gameInProgress);
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
	}
	
	/**
	 * 
	 */
	public void run(){
		System.out.println("logic Manager started");
		Message m;
		String command;
		String uuid;
		Player p;
		while(!gameInProgress){
			Thread.yield();
		}
		System.out.println("gameInProgress now!");
		System.out.println("game is now in progress");
		try{
			while(playerCount > 0){
				System.out.println("attempting to read command ...");
				m = commandQueue.take();
				System.out.println("got command in logic manager");
				
				command = new String(m.datagram.getData()).trim();
				System.out.println("logic command is: "+command);
				uuid = m.datagram.getAddress().toString() + m.datagram.getPort();
				
				//System.out.println("Execute Command/PlayerID Pair - "+command+":"+uuid);
				
				Object[] users = userManager.getCurrentPlayerList().toArray();
				for(int i =0;i < users.length;i++){
					User u = (User)users[i];
					p = u.getPlayer();
					if(u.getUUID().equals(uuid)){
						System.out.println("matching id");
						System.out.println(p.getName());
						System.out.println("(" + p.getPosX() + "," + p.getPosY()+")");
						if(command.equals("UP")){
							if (!safeMove(p.getPosX(), p.getPosY() - 1)){
								p.loseLife();
								removePlayerFromGameBoard(p);
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
									
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() - 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveUp();
									board.set(p,p.getPosX(),p.getPosY());
									Logger.acceptMessage(u.getUUID() + " move UP");
							}
							else if(board.hasDoor(p.getPosX(), p.getPosY() - 1)){
								playerCount--;
								userManager.moveCurrentToFuture(u);
							}
								
						}
						else if(command.equals("DOWN")){
							if (!safeMove(p.getPosX(), p.getPosY() + 1)){
								p.loseLife();
								removePlayerFromGameBoard(p);
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveDown();
									board.set(p,p.getPosX(),p.getPosY());
									Logger.acceptMessage(u.getUUID() + " move DOWN");
							}
							else if(board.hasDoor(p.getPosX(), p.getPosY() + 1)){
								playerCount--;
								userManager.moveCurrentToFuture(u);
							}
						}
						else if(command.equals("LEFT")){
							if (!safeMove(p.getPosX()-1, p.getPosY())){
								p.loseLife();
								removePlayerFromGameBoard(p);
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX()-1, p.getPosY())){
									board.remove(p.getPosX(), p.getPosY());
									p.moveLeft();
									board.set(p,p.getPosX(),p.getPosY());
									Logger.acceptMessage(u.getUUID() + " move LEFT");
							}
							else if(board.hasDoor(p.getPosX()-1, p.getPosY())){
								playerCount--;
								userManager.moveCurrentToFuture(u);
							}
						}
						else if(command.equals("RIGHT")){
							if (!safeMove(p.getPosX() + 1, p.getPosY())){
								p.loseLife();
								removePlayerFromGameBoard(p);
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX()+1, p.getPosY())){
									board.remove(p.getPosX(), p.getPosY());
									p.moveRight();
									board.set(p,p.getPosX(),p.getPosY());
									Logger.acceptMessage(u.getUUID() + " move RIGHT");
							}
							else if(board.hasDoor(p.getPosX()+1, p.getPosY())){
								playerCount--;
								userManager.moveCurrentToFuture(u);
							}
						}
						else if(command.equals("END_GAME")){
							playerCount--;
							userManager.moveCurrentToFuture(u);
						}
						//else if(command.equals("BOMB"))
					}
				}
				//board.update();
				networkManager.sendBoardToAllClients(board.toString());
				Logger.acceptMessage("Board sent to all client");
				Logger.acceptMessage(board.toString());
			}
			networkManager.sendEndGameToAllClients();
			Logger.writeLog();
			Logger.endLog();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
