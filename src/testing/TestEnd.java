package testing;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;

import serverLogic.ServerMain;

/**
 * 
 * @author draymire
 *
 * For testing of ending the game
 *
 */
public class TestEnd {

	
	//Initialize player ports and a testing Semaphore
	ServerMain server = null;
	int playerOnePort = 8878;
	int playerTwoPort = 8869;
	Semaphore testSem = new Semaphore(0);
	
	@Test
	/**
	 * Test a singleplayer game
	 */
	public void singlePlayerGameSession(){

		//Start the server
		server = new ServerMain(testSem, 3);
		
		//Initialize the file to use in the TestDriver
		String filename = "/TestingFiles/End/SinglePlayerGameSessionEnd";
		
		//Run the TestDriver
		new TestDriver(filename, playerOnePort);
		
		
		try{//TODO: Set timeout to a logic length
			//Waits for the 
			testSem.wait(timeout);
			
			//Assert that the player ended the game
			//within the specified time.
			assertEquals(1,testSem.availablePermits());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 
	 * @param mode
	 */
	public void multiPlayerGameSession(int mode){
		
		
		server = new ServerMain(testSem, 3);
		
		String playerOneFile = "/TestingFiles/End/SinglePlayerGameSessionEnd";
		String playerTwoFile;
		
		if (mode == 0)
			playerTwoFile = "TestingFiles/Win/MultiPlayerGameSessionWin.player2";
		else if(mode == 1)
			playerTwoFile = "/TestingFiles/End/MutliPlayerGameSessionEnd.player2";
		
		new TestDriver(playerOneFile, playerOnePort);
		new TestDriver(playerTwoFile, playerTwoPort);
		
		
		try{//TODO: Set timeout to a logic length
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
