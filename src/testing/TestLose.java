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
 */
public class TestLose {

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
	 * Test a singleplayer
	 */
	public void singlePlayerGameSession(){

		//Start the server
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 1);
		
		//Initialize the file to use in the TestDriver
		String filename = "./TestingFiles/Lose/SinglePlayerGameSessionLose";
		
		//Run the TestDriver
		TestDriver driverOne = new TestDriver(filename, playerOnePort, "SingleDriver");
		
		System.out.println("Sleeping for 15");
		
		try{
			Thread.sleep(timeout);
		}catch (Exception e){
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
	public void TwoPlayersBothLose(){
		//Start the server		
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 1);
		playerOnePort = 8879;

		//Initialize the files to use in the TestDrivers		
		String playerOneFile = "./TestingFiles/Lose/SinglePlayerGameSessionLose";
		String playerTwoFile = "./TestingFiles/Lose/MultiPlayerGameSessionLose.player2";
		
		//Run the TestDrivers
		TestDriver driverOne = new TestDriver(playerOneFile, playerOnePort, "MultiDriverOne");
		TestDriver driverTwo = new TestDriver(playerTwoFile, playerTwoPort, "MultiDriverTwo");
		
		
		try{
			Thread.sleep(20000);
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		server.shutdown();
		driverOne.shutdown();
		driverTwo.shutdown();
		assertEquals(2,testSem.availablePermits());
	}
	
	@Test
	/**
	 * 
	 * Test Multiple Players at once. 
	 * 
	 * @param mode
	 */
	public void TwoPlayersOneLose(){
		//Start the server		
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 1);
		playerOnePort = 8879;

		//Initialize the files to use in the TestDrivers		
		String playerOneFile = "./TestingFiles/Lose/SinglePlayerGameSessionLose";
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

	/*@Test
	/**
	 * Testing a multiplayer game
	 * 	Test both players losing (mode 1)
	 * 	Test one player losing (mode 0)
	 *
	 * @param mode
	 */
	/*public void multiPlayerGameSession(int mode){
		
		
		//Start the server
		server = new ServerMain(testSem, 1);
		
		//Initializes the file to use in the TestDrivers
		String playerOneFile = "/TestingFiles/Lose/SinglePlayerGameSessionLose";
		String playerTwoFile = null;
		
		if (mode == 0)
			playerTwoFile = "TestingFiles/End/MultiPlayerGameSessionEnd.player2";
		else if(mode == 1)
			playerTwoFile = "./TestingFiles/Lose/MutliPlayerGameSessionLose.player2";
		
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
		
		
	}*/

}
