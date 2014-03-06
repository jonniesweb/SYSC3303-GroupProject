package serverLogic;

import java.lang.String;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	private int playerCount;
	
	private Logger log;
	private boolean gameInProgress;
	/**
	 * 
	 * @param uManager
	 * @param l
	 * @param nManager
	 */
	public LogicManager(UserManager uManager, Logger l){
		// initialize board
		this.board = new GameBoard();
		placePlayers(board, uManager);
		this.userManager = uManager;
		this.playerCount = uManager.getCurrentPlayerList().size();
		this.log = l;
		// start the run method
		new Thread(this).start();
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
		if(entity instanceof Bomb || entity instanceof Wall) { return false; }
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
		
		int i = 0;
		int x;
		int y;

		for (User u : users.getCurrentPlayerList()) {
			if (i < 2) {
				x = 0;
				y = (i % 2) * board.getHeight();
			} else {
				x = board.getWidth();
				y = (i % 2) * board.getHeight();
			}
			i++;
			board.set(u.getPlayer(), x, y);
			u.getPlayer().setPos(x, y);
		}

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
	}
	public void setNetworkManager(NetworkManager m){
		this.networkManager = m;
	}
	
	
	/**
	 * 
	 */
	public void run(){
		Message m;
		String command;
		String uuid;
		Player p;
		
		try{
			while(playerCount > 0){
				
				m = commandQueue.take();
				
				command = new String(m.datagram.getData());
				uuid = m.datagram.getAddress().toString() + m.datagram.getPort();
				
				//System.out.println("Execute Command/PlayerID Pair - "+command+":"+uuid);
				
				
				for(User u: userManager.getCurrentPlayerList()){
					p = u.getPlayer();
					if(u.getUUID().equals(uuid)){
						if(command.equals("UP")){
							if (!safeMove(p.getPosX(), p.getPosY() + 1)){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveUp();
									board.set((Entity)p,p.getPosX(),p.getPosY());
								}
						}
						else if(command.equals("DOWN")){
							if (!safeMove(p.getPosX(), p.getPosY() - 1)){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveDown();
									board.set((Entity)p,p.getPosX(),p.getPosY());
									
								}
						}
						else if(command.equals("LEFT")){
							if (!safeMove(p.getPosX() - 1, p.getPosY())){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveLeft();
									board.set((Entity)p,p.getPosX(),p.getPosY());
								}
						}
						else if(command.equals("RIGHT")){
							if (!safeMove(p.getPosX() + 1, p.getPosY())){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){
									board.remove(p.getPosX(), p.getPosY());
									p.moveRight();
									board.set((Entity)p,p.getPosX(),p.getPosY());
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
				log.acceptMessage(board.toString());
			}
			
			networkManager.sendEndGameToAllClients();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
