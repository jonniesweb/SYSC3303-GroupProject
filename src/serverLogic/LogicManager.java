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

/**
 * 
 * @author zachstoner
 *
 */
public class LogicManager implements Runnable {
	
	private BlockingQueue<String> commandQueue = new LinkedBlockingQueue<String>();
	private BlockingQueue<String> playerQueue = new LinkedBlockingQueue<String>();
	
	private List<Player> playerList;
	private boolean gameInProgress;
	private GameBoard board;
	
	
	
	public LogicManager(List<Player> playerList2, boolean gameState){
		this.playerList = playerList2;
		this.board = new GameBoard(playerList);
		gameInProgress = gameState;
	}
	
	/**
	 * 
	 * @param command
	 * @param playerID
	 */
	public void execute(String command, String playerID){
		try{
			commandQueue.put(command);
			playerQueue.put(playerID);
			
			
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
	private boolean checkGameEnd(){
		for(Player p: playerList){
				if(p.isAlive())
					return false;
		}
		return true;
	}
	
	//TODO: init gameBoard with playerList setGameInProgress = true
	public void start(){
		gameInProgress = true;
		board = new GameBoard(playerList);
		
	}
	//TODO: return gameboard as string so we can send to all players
	public String getBoard(){
		return null;
	}
	/**
	 * 
	 */
	public void run(){
		String command;
		String playerID;
		Entity entity;
		try{
			while(true){
				if(gameInProgress){
					command = commandQueue.take();
					playerID = playerQueue.take();
					System.out.println("Execute Command/PlayerID Pair - "+command+":"+playerID);
					for(Player p: playerList){
						if(p.getName().equals(playerID)){
						
							if(command.equals("UP")){
								if (!safeMove(p.getPosX(), p.getPosY() + 1)){p.loseLife();}
								else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveUp();}
							}
							else if(command.equals("DOWN")){
								if (!safeMove(p.getPosX(), p.getPosY() - 1)){p.loseLife();}
								else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveDown();}
							}
							else if(command.equals("LEFT")){
								if (!safeMove(p.getPosX() - 1, p.getPosY())){p.loseLife();}
								else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveLeft();}
							}
							else if(command.equals("RIGHT")){
								if (!safeMove(p.getPosX() + 1, p.getPosY())){p.loseLife();}
								else if (validMove(p.getPosX(), p.getPosY() + 1)){p.moveRight();}
							}
						//else if(command.equals("BOMB"))
						}
					}
				}
					if(checkGameEnd()){
						gameInProgress = false;
					}
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
