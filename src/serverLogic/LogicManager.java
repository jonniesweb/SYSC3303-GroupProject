package serverLogic;
//TODO: add start method that resets gameBoard with all players in player List and sets
// gameInpgress to true

//TODO: add method to tell when game is over

//TODO: init gameBoard with playerList setGameInProgress = true

//TODO: return gameboard as string so we can send to all players
import java.lang.String;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.LinkedList;

import entities.Entity;
import entities.Bomb;
import entities.Door;
import entities.Enemy;
import entities.Explosion;
import entities.Player;
import entities.PowerUp;
import entities.Wall;

import gameLogic.GameBoard;

import Networking.Message;

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
	
	
	public LogicManager(UserManager uManager, NetworkManager nManager){
		this.board = new GameBoard(uManager);
		networkManager = nManager;
		userManager = uManager;
		playerCount = (uManager.getPlayerList()).size();
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
		Entity entity = board.getEntityAt(x,y);
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
		Entity entity = board.getEntityAt(x,y);
		if(entity instanceof Bomb || entity instanceof Wall) { return false; }
		else { return true; }
	}
	
	public String getBoard(){
		return board.toString();
	}
	
	/**
	 * 
	 */
	public void run(){
		Message m;
		String command;
		String playerIP;
		Player p;
		
		try{
			while(playerCount > 0){
				
				m = commandQueue.take();
				
				command = new String(m.datagram.getData());
				playerIP = m.datagram.getAddress().toString();
				
				System.out.println("Execute Command/PlayerID Pair - "+command+":"+playerIP);
				
				
				for(User u: userManager.getCurrentPlayerList()){
					p = u.player;
					if(u.ip.equals(playerIP)){
						if(command.equals("UP")){
							if (!safeMove(p.getPosX(), p.getPosY() + 1)){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveUp();}
						}
						else if(command.equals("DOWN")){
							if (!safeMove(p.getPosX(), p.getPosY() - 1)){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveDown();}
						}
						else if(command.equals("LEFT")){
							if (!safeMove(p.getPosX() - 1, p.getPosY())){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveLeft();}
						}
						else if(command.equals("RIGHT")){
							if (!safeMove(p.getPosX() + 1, p.getPosY())){
								p.loseLife();
								if(!p.isAlive()){
									playerCount--;
									userManager.moveCurrentToFuture(u);
								}
							}
							else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveRight();}
						}
						else if(command.equals("END_GAME")){
							playerCount--;
							userManager.moveCurrentToFuture(u);
						}
						//else if(command.equals("BOMB"))
					}
				}
				networkManager.sendBoardToAllClients();
			}
			
			networkManager.sendEndGameToAllClients();
			//networkManager.sendMessage(board.getBoard(), )
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
