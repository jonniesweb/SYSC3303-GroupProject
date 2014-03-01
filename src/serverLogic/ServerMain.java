package serverLogic;
import Networking.*;
import gameLogic.*;
import testing.Logger;


public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		boolean running = true;
		
		Thread[] threads = new Thread[3];
		
		while(running){
			threads[0] = new Thread(new Network(8888), "Networking Main Thread");
			threads[1] = new Thread(new GameBoard(), "Gameboard thread");
			threads[2] = new Thread(new Logger(), "Logging Thread");
			
			for (int i = 0; i < 3; i++){
				threads[i].start();
			}
			
			for(int i = 0; i < 3; i ++){
				try{
					threads[i].join();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		
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
