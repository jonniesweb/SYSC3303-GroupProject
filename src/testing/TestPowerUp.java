package testing;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Semaphore;

import serverLogic.ServerMain;

import java.util.List;
import java.util.ArrayList;

import entities.Player;
import serverLogic.User;

public class TestPowerUp {
	
	ServerMain server = null;
	int playerOnePort = 8878;
	Semaphore testSem = new Semaphore(0);
	long timeout = 15000;
	
	@Test
	public void testPowerUpFind(){
		//Start the server
		server = new ServerMain(testSem, 3);
		
		//Initialize the file to use in the TestDriver
		String filename = "/TestingFiles/PowerUp/PowerUpFind";
		
		//Run the TestDriver
		TestDriver driverOne = new TestDriver(filename, playerOnePort);
		
		
		try{//TODO: Set timeout to a logic length
			//Waits for the the players to end or the timeout occurs
			Thread.sleep(timeout);
			
			List<User> list = new ArrayList<User>();
			list.addAll(server.getCurrentPlayerList());
			
			Player p = list.get(0).getPlayer();
			
			//Assert the player picked up the powerup
			assertTrue(p.getBombRangePowerUpEnabled());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}

		server.shutdown();
		driverOne.shutdown();
	}

}
