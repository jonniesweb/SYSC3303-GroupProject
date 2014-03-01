package serverLogic;

import gameLogic.*;
import testing.Logger;
import java.util.concurrent.Executors;



public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameBoard game = new GameBoard();
		
		Thread[] threads = new Thread[3];
		threads[0] = new Thread(new Networking(), "Networking Main Thread");
		threads[1] = new Thread(new GameBoard(), "Gameboard thread");
		threads[2] = new Thread(new Logger(), "Logging Thread");
		
		
		
		//	Begin listening
		//	Loop
		//		Establish Connection
		//		Populate Gameboard
		// 		Loop
		//			Send Gameboard Status
		//			Check for and Execute Commands
		// 			Update Gameboard
		//			Check Player Life Status
		// 		Kill Connection
		// 		Wait for new connection
		
		
		

	}

}
