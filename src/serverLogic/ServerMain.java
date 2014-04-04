package serverLogic;

//import entities.Entity;
import entities.Door;
import entities.PowerUp;
import gameLogic.GameBoard;
import testing.Logger;

import java.util.Collection;
import java.util.concurrent.Semaphore;

import Networking.Network;

// TODO: add command line arguments to allow starting up from a predefined,
// gameboard, or randomly generating one

/**
 * 
 * 
 *
 */
public class ServerMain {
	
	private static final int NUM_PLAYERS = 2;
	
	// members
	LogicManager logicManager;
	NetworkManager networkManager;
	UserManager userManager;
	Logger logger;
	
	Semaphore testSem;
	
	public ServerMain(int port) {
		
		// initialize all server components
		
		userManager = new UserManager(NUM_PLAYERS);
		logger = new Logger();
		logicManager = new LogicManager(userManager);
		networkManager = new NetworkManager(logicManager,userManager,port);
		logicManager.setNetworkManager(networkManager);
		//EnemyManager e = new EnemyManager(logicManager);
	}
	
	public ServerMain(Semaphore s, int tMode){
		this.testSem = s;
		userManager = new UserManager(NUM_PLAYERS);
		logger = new Logger();
		logicManager = new LogicManager(userManager, testSem, tMode);
		networkManager = new NetworkManager(logicManager,userManager,Network.SERVER_PORT_NO);
		logicManager.setNetworkManager(networkManager);
		
	}
	
	public ServerMain(boolean door) {
		this(Network.SERVER_PORT_NO);
		
		GameBoard board = new GameBoard(20, 20);
		if (door) {
			
			logicManager.setGameBoard(board);
		} else {
			board.set(new Door(3, 3));
			logicManager.setGameBoard(board);
		}
	}
	
	/**
	 * Testing hook function
	 */
	public Collection<User> getCurrentPlayerList() {
		return userManager.getCurrentPlayerList();
	}
	
	public void setPowerUp(){
		
		GameBoard board = logicManager.getGameBoard();
		board.set(new PowerUp(1,0));
		
		logicManager.setGameBoard(board);
	}

	
	public void shutdown(){
		logicManager.shutdown();
		networkManager.shutdown();
		
		
		System.out.println("Server shutdown() executed");
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0)
			new ServerMain(8888).logicManager.setGameBoard(new GameBoard(7, 7));
		for(int i =0; i< args.length;i++)
		new ServerMain(Integer.parseInt(args[i])).logicManager.setGameBoard(new GameBoard(7, 7));
		//new ServerMain(8887).logicManager.setGameBoard(new GameBoard(7, 7));
		
		

		
		///????/ who inits player positions when they are added??????
		//		Gameboard will init them when it places them into the board
		
		
		// Network THREAD
		//
		// Loop
		//		If START_GAME from P1
		//			set GAMEINPROGRESS to true
		//		If NOT GAMEINPROGRESS, then
		//			If JOIN_GAME, add to player list
		//		else
		//			add to spectator list, then add them to the new game afterwards
		//		If SPECTATOR_JOIN
		//			add to spectators
		//		If END_GAME remove player
		//			If GAMEINPROGRESS && players == 0 
		//				stop game, set GAMEINPROGRESS to false, wait for players
		//      IF GAMEINPROGRESS,
		//			IF GAMECOMMAND
		//				notify()
		
		
		//	Update Thread/ logic thread
		//
		//	
		//	Loop
		//		wait()
		//		IF COMMAND
		//			do command
		//		send GameBoard to all players
		//		log gameboard
		
		
		// 	Bomb Thread - for later
		//		
		
		//	Player Manager Class
		// 	members: currentPlayerList, futurePlayerList, observerList
		//	getCurrentPlayers(): PlayerEntityList
		
		
		// logic class
		// action(player, action)

	}
	


}
