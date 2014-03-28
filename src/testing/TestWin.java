package testing;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;

import serverLogic.ServerMain;

/**
 * 
 * @author draymire
 *
 * For testing of winnning the game (aka the door being found)
 *
 */
public class TestWin {

	
	//Initialize player ports and a testing Semaphore
	ServerMain server = null;
	int playerOnePort = 8878;
	int playerTwoPort = 8869;
	Semaphore testSem = new Semaphore(0);
	long timeout = 5000;
	
	@Test
	/**
	 * Test a single player playing by themselves
	 */
	public void singlePlayerGameSession(){

		//Start the server
		server = new ServerMain(testSem, 2);
		
		//Initialize the file to use in the TestDriver
		String filename = "./TestingFiles/Win/SinglePlayerGameSessionWin";
		
		//Run the TestDriver
		new TestDriver(filename, playerOnePort);
		
		
		try{//TODO: Set timeout to a logic length
			//Wait the specified time before checking
			//to see if the player has won
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
	 * 
	 * Test Multiple Players at once. 
	 * 	Test both players winning (mode 1)
	 * 	Test one player winning (mode 0)
	 * 
	 * @param mode
	 */
	public void multiPlayerGameSession(int mode){
		
		//Start the server		
		server = new ServerMain(testSem, 2);

		//Initialize the files to use in the TestDrivers		
		String playerOneFile = "./TestingFiles/Win/SinglePlayerGameSessionWin";
		String playerTwoFile = null;
		
		if (mode == 0)
			playerTwoFile = "./TestingFiles/End/MultiPlayerGameSessionEnd.player2";
		else if(mode == 1)
			playerTwoFile = "./TestingFiles/Win/MutliPlayerGameSessionWin.player2";
		
		//Run the TestDrivers
		new TestDriver(playerOneFile, playerOnePort);
		new TestDriver(playerTwoFile, playerTwoPort);
		
		
		try{//TODO: Set timeout to a logic length
			//Wait the specified time before checking
			//to see if the player has won
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
