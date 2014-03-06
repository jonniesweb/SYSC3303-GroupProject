package serverLogic;

import testing.Logger;

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
	
	public ServerMain() {
		
		// initialize all server components
		networkManager = new NetworkManager();
		userManager = new UserManager(NUM_PLAYERS);
		logger = new Logger();
		logicManager = new LogicManager(userManager, logger, networkManager);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
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
