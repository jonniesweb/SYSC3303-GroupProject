package testing;

import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;

import serverLogic.ServerMain;

/**
 * 
 * @author draymire
 *
 * For testing of winning the game (aka the door being found)
 *
 */
public class TestWin {

	
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
	 * Test a single player playing by themselves
	 */
	public void singlePlayerGameSession(){

		//Start the server
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 2);
		
		//Initialize the file to use in the TestDriver
		String filename = "./TestingFiles/Win/SinglePlayerGameSessionWin";
		
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
	public void TwoPlayersBothWin(){
		//Start the server		
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 2);
		playerOnePort = 8879;

		//Initialize the files to use in the TestDrivers		
		String playerOneFile = "./TestingFiles/Win/SinglePlayerGameSessionWin";
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
		assertEquals(2,testSem.availablePermits());
	}
	
	@Test
	/**
	 * 
	 * Test Multiple Players at once. 
	 * 
	 * @param mode
	 */
	public void TwoPlayersOneWins(){
		//Start the server		
		testSem = new Semaphore(0);
		server = new ServerMain(testSem, 2);
		playerOnePort = 8879;

		//Initialize the files to use in the TestDrivers		
		//String playerOneFile = "./TestingFiles/Win/SinglePlayerGameSessionWin";
		String playerTwoFile = "./TestingFiles/Win/MultiPlayerGameSessionWin.player2";
		
		//Run the TestDrivers
		TestDriver driverOne = new TestDriver(playerTwoFile, playerOnePort, "MultiDriverOne");
		TestDriver driverTwo = new TestDriver(playerTwoFile, playerTwoPort, "MultiDriverTwo");
		
		
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
