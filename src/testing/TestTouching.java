package testing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;

import serverLogic.ServerMain;

public class TestTouching {
	
	//Initialize player ports and a testing Semaphore
	ServerMain server = null;
	int playerOnePort = 8878;
	int playerTwoPort = 8869;
	Semaphore testSem = new Semaphore(0);
	long timeout = 5000;
	
	@Test
	/**
	 * Test players touching
	 */
	public void PlayersTouching(){

		//Start the server
		server = new ServerMain(testSem, 1);
		
		//Initialize the file to use in the TestDriver
		String playerOneFile = "/TestingFiles/Touching/MultiPlayerGameSessionTouching";
		String playerTwoFile = "/TestingFiles/Touching/MultiPlayerGameSessionTouching";
		
		//Run the TestDriver
		new TestDriver(playerOneFile, playerOnePort);
		new TestDriver(playerTwoFile, playerTwoPort);
		
		try{//TODO: Set timeout to a logic length
			//Waits for the the players to end or the timeout occurs
			testSem.wait(timeout);
			
			//Assert that one player died
			//within the specified time.
			assertEquals(1,testSem.availablePermits());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
