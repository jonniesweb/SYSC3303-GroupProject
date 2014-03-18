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
public class TestEnd {

	ServerMain server = null;
	int playerOnePort = 8878;
	int playerTwoPort = 8869;
	Semaphore testSem = new Semaphore(0);
	
	@Test
	/**
	 * 
	 */
	public void singlePlayerGameSession(){

		server = new ServerMain(testSem, 3);
		
		String filename = "/TestingFiles/End/SinglePlayerGameSessionEnd";
		
		new TestDriver(filename, playerOnePort);
		
		
		try{//TODO: Set timeout to a logic length
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
