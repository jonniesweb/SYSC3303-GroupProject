package serverLogic;
import entities.Player;
import Networking.*;
import gameLogic.*;
import testing.Logger;
import java.util.concurrent.Semaphore;


public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		boolean running = true;
		
		Thread thread = new Thread(new NetworkManager());
		
		while(running){
			try{
				thread.start();
				thread.join();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		///????/ who inits player positions when they are added??????
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
