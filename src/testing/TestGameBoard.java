package testing;

import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import entities.Door;
import entities.Player;
import entities.PowerUp;
import entities.Enemy;
import entities.Wall;
import entities.Entity;
import gameLogic.GameBoard;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGameBoard {
	GameBoard game;
	
	//@Before
	public void setUp() throws Exception {
		game = new GameBoard(7,7);
		//System.out.println("Before the test exectued");
	}

	/*@After
	public void tearDown() throws Exception {
		
	}*/

	@Test
	public void testRandomize() throws Exception {
		setUp();
		game.randomizeFloor(1);
		
		//Corner of board shouldnt have wall initialized
		assertFalse(game.get(0, 0) instanceof Wall);
		assertFalse(game.get(0, 1) instanceof Wall);
		assertFalse(game.get(1, 0) instanceof Wall);
		
		setUp();
		//randomize floor with 2 players
		//and check top left and bot right corner
		game.randomizeFloor(2);
		assertFalse(game.get(6, 6) instanceof Wall);
		assertFalse(game.get(6, 5) instanceof Wall);
		assertFalse(game.get(5, 6) instanceof Wall);
		
		setUp();
		//randomize floor with 3 players
		//and check top left and bot right corner and left bot
		game.randomizeFloor(3);
		assertFalse(game.get(6, 0) instanceof Wall);
		assertFalse(game.get(5, 0) instanceof Wall);
		assertFalse(game.get(6, 1) instanceof Wall);
		
		setUp();
		//randomize floor with 4 players
		//and check top left and bot right corner and left bot and top right
		game.randomizeFloor(4);
		assertFalse(game.get(0, 6) instanceof Wall);
		assertFalse(game.get(0, 5) instanceof Wall);
		assertFalse(game.get(1, 6) instanceof Wall);
			
	}
	@Test
	public void testSetEntity() throws Exception{
		setUp();
		game.randomizeFloor(2);
		//Set one player on top left corner
		//check if top left corner is instance of player
		game.set(new Player(0,0,"P1"), 0, 0);
		assertTrue(game.get(0, 0) instanceof Player);
		assertEquals(((Player)game.get(0, 0)).getName(),"P1");
		
		//Set one player on top left corner
		//check if top left corner is instance of player
		game.set(new Player(6,6,"P2"), 6, 6);
		assertTrue(game.get(6, 6) instanceof Player);
		assertEquals(((Player)game.get(6, 6)).getName(),"P2");

	}
	@Test
	public void testRemoveEntity() throws Exception{
		setUp();
		game.randomizeFloor(2);
		game.set(new Player(0,0,"P1"), 0, 0);
		game.remove(0, 0);
		assertFalse(game.get(0, 0) instanceof Player);
	}
	@Test
	public void testGenerateFloorFromFile() throws Exception{
		setUp();
		game.generateFloor("FloorTest.txt");
		assertFalse(game.get(0, 0) instanceof Wall);
		assertFalse(game.get(6, 6) instanceof Wall);
		//assertTrue(game.get(0, 3) instanceof Wall);
		
	}
	@Test
	public void testDoor() throws Exception{
		System.out.println("");
		System.out.println("-------------------------");
		System.out.println("TestDoor");
		setUp();
		Entity d = game.getDoor();
		if(d instanceof Door) System.out.println("It's a door");
		System.out.println("-------------------------");
		System.out.println("");
		assertTrue(game.getDoor() instanceof Door);	
	}
	
	@Test
	public void testGameBoardSerialized() throws Exception {
		setUp();
		
		char[] charBoard = game.toString().toCharArray();
		byte[] byteBoard = game.toString().getBytes();
		System.out.println("printing byte boarding");
		/*for (int i = 0; i < charBoard.length; i++) {
			byteBoard[i] = (byte) charBoard[i];
		}*/
		
		for (int i = 0; i < byteBoard.length; i++) {
			System.out.print(charBoard[i]);
		}
		GameBoard newBoard = new GameBoard(new String(byteBoard).toCharArray());
		System.out.println("printing gameboard");
		System.out.println();
		System.out.println(game);
		System.out.println("-----------");
		System.out.println(newBoard);
		
		assertTrue(newBoard.toString().equals(game.toString()));
	}
	
	@Test
	public void testEnemyExistance() throws Exception {
		
	//TODO testEnemyExistance()
		//Make sure that the number of expected Enemies actually exist
		
		
		
		//Setup the board
		//place an expected number of enemies on the board
		//iterate over the board counting number of enemies
		//assertEquals(expectedNumber, actualNumber)
	}
	@Test
	public void testPowerUpExistance() throws Exception {
	//	game = new GameBoard(7,7);
		setUp();
		System.out.println("testPowerUpExistance1");
		//Make sure that the expected number of powerups actually exist
		
		game.randomizeFloor(2);
		int powerUpCount = 0;
		
		Entity[][] board = game.getBoard();
		System.out.println("\nboard -------------------");
		for(int i = 0; i < game.getWidth(); i++){
			for(int k = 0; k < game.getHeight(); k++){
			
				if(board[i][k] instanceof Door)
					System.out.print(" D");
				else if(board[i][k] instanceof Wall)
					System.out.print(" W");
				
				else if(board[i][k] instanceof PowerUp){
					powerUpCount++;	
					System.out.print(" P");
				} else {
					System.out.print(" .");
				}
			}
			System.out.println("");
		}
		System.out.println("board -------------------\n");
		System.out.println("PowerUpCount " + powerUpCount);
		assertEquals(2, powerUpCount);
	}
	
	public static void main(String[] args) {
		System.out.println("Something something main");
      Result result = JUnitCore.runClasses(TestGameBoard.class);
      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
      System.out.println(result.wasSuccessful());
	}
}
