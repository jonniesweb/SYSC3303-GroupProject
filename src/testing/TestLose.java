package testing;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;

import serverLogic.ServerMain;

/**
 * 
 * @author draymire
 *
 */
public class TestLose {

	//Initialize player ports and a testing Semaphore
	ServerMain server = null;
	int playerOnePort = 8878;
	int playerTwoPort = 8869;
	Semaphore testSem = new Semaphore(0);
	
	@Test
	/**
	 * Test a singleplayer
	 */
	public void singlePlayerGameSession(){

		//Start the server
		server = new ServerMain(testSem, 1);
		
		//Initialize the file to use in the TestDriver
		String filename = "/TestingFiles/Lose/SinglePlayerGameSessionLose";
		
		//Run the TestDriver
		new TestDriver(filename, playerOnePort);
		
		
		try{//TODO: Set timeout to a logic length
			//Waits for the the players to end or the timeout occurs
			testSem.wait(timeout);
			
			//Assert that the player managed to get to the door 
			//within the specified time.
			assertEquals(1,testSem.availablePermits());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Testing a multiplayer game
	 * 	Test both players losing (mode 1)
	 * 	Test one player losing (mode 0)
	 *
	 * @param mode
	 */
	public void multiPlayerGameSession(int mode){
		
		
		//Start the server
		server = new ServerMain(testSem, 1);
		
		//Initializes the file to use in the TestDrivers
		String playerOneFile = "/TestingFiles/Lose/SinglePlayerGameSessionLose";
		String playerTwoFile;
		
		if (mode == 0)
			playerTwoFile = "TestingFiles/End/MultiPlayerGameSessionEnd.player2";
		else if(mode == 1)
			playerTwoFile = "/TestingFiles/Lose/MutliPlayerGameSessionLose.player2";
		
		//Run the TestDrivers
		new TestDriver(playerOneFile, playerOnePort);
		new TestDriver(playerTwoFile, playerTwoPort);
		
		
		try{//TODO: Set timeout to a logic length
			//Waits for the the players to end or the timeout occurs
			testSem.wait(timeout);
			
			//Assert that the player managed to get to the door 
			//within the specified time.
			
			if(mode == 0)
				assertEquals(1,testSem.availablePermits());
			
			//Assert that the players managed to get to the door
			else if (mode == 1)
				assertEquals(2,testSem.availablePermits());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
