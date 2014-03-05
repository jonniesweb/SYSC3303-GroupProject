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
	
	/**
	 * 
	 * @param uManager
	 * @param l
	 * @param nManager
	 */
	public LogicManager(UserManager uManager, Logger l, NetworkManager nManager){
		this.board = new GameBoard(uManager);
		this.networkManager = nManager;
		this.userManager = uManager;
		this.playerCount = (uManager.getCurrentPlayerList()).size();
		this.log = l;
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
	
	/**
	 * 
	 * @return
	 */
	public String getBoard(){
		return board.toString();
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
				log.acceptMessage(board.toString());
			}
			
			networkManager.sendEndGameToAllClients();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
