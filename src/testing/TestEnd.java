package testing;

import org.junit.After;
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
	long timeout = 15000;
	
	@After
	public void after(){
		System.out.println("\n\n\n\n\n\n\n\n");
		System.out.println("==================");
		
	}
	
	@Test
	/**
	 * Test a singleplayer game
	 */
	public void singlePlayerGameSession(){

		//Start the server
		server = new ServerMain(testSem, 3);
		
		//Initialize the file to use in the TestDriver
		String filename = "./TestingFiles/End/SinglePlayerGameSessionEnd";
		
		//Run the TestDriver
		TestDriver driverOne = new TestDriver(filename, playerOnePort);
		
		
		try{//TODO: Set timeout to a logic length
			//Waits for the the players to end or the timeout occurs
			Thread.sleep(timeout);
			
			//Assert that the player ended the game
			//within the specified time.
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		server.shutdown();
		driverOne.shutdown();
		assertEquals(1,testSem.availablePermits());
	}
	
	@Test
	/**
	 * 
	 * Test Multiple Players at once. 
	 * 
	 * @param mode
	 */
	public void TwoPlayersBothEnd(){
		
		System.out.println("Starting BothEnd");
		
		//Start the server		
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 3);
		playerOnePort = 8879;

		//Initialize the files to use in the TestDrivers		
		String playerOneFile = "./TestingFiles/End/SinglePlayerGameSessionEnd";
		String playerTwoFile = "./TestingFiles/End/MultiPlayerGameSessionEnd.player2";
		
		//Run the TestDrivers
		TestDriver driverOne = new TestDriver(playerOneFile, playerOnePort, "MultiDriverOne");
		TestDriver driverTwo = new TestDriver(playerTwoFile, playerTwoPort, "MultiDriverTwo");
		
		
		try{
			Thread.sleep(20000);
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		
		driverOne.shutdown();
		driverTwo.shutdown();
		server.shutdown();
		
		
		assertEquals(2,testSem.availablePermits());
	}
	
	@Test
	/**
	 * 
	 * Test Multiple Players at once. 
	 * 
	 * @param mode
	 */
	public void TwoPlayersOneEnds(){
		//Start the server		
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 3);
		playerOnePort = 8879;

		//Initialize the files to use in the TestDrivers		
		String playerOneFile = "./TestingFiles/End/SinglePlayerGameSessionEnd";
		String playerTwoFile = "./TestingFiles/Win/MultiPlayerGameSessionWin.player2";
		
		//Run the TestDrivers
		TestDriver driverOne = new TestDriver(playerTwoFile, playerOnePort, "MultiDriverOne");
		TestDriver driverTwo = new TestDriver(playerOneFile, playerTwoPort, "MultiDriverTwo");
		
		
		try{
			Thread.sleep(20000);
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		server.shutdown();
		driverOne.shutdown();
		driverTwo.shutdown();
		assertEquals(1,testSem.availablePermits());
	}
}
