package testing;

import static org.junit.Assert.*;
import entities.Door;
import entities.Player;
import entities.Wall;
import gameLogic.GameBoard;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGameBoard {
	GameBoard game;
	
	@Before
	public void setUp() throws Exception {
		game = new GameBoard(7,7);
	}

	@After
	public void tearDown() throws Exception {
		
	}

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
		game.set(new Player(0,0,"Fucker"), 0, 0);
		assertTrue(game.get(0, 0) instanceof Player);
		assertEquals(((Player)game.get(0, 0)).getName(),"Fucker");
		
		//Set one player on top left corner
		//check if top left corner is instance of player
		game.set(new Player(6,6,"Fucker2"), 6, 6);
		assertTrue(game.get(6, 6) instanceof Player);
		assertEquals(((Player)game.get(6, 6)).getName(),"Fucker2");

	}
	@Test
	public void testRemoveEntity() throws Exception{
		setUp();
		game.randomizeFloor(2);
		game.set(new Player(0,0,"Fucker"), 0, 0);
		game.remove(0, 0);
		assertFalse(game.get(0, 0) instanceof Player);
	}
	@Test
	public void testGenerateFloorFromFile() throws Exception{
		setUp();
		game.generateFloor("FloorTest.txt");
		assertFalse(game.get(0, 0) instanceof Wall);
		assertFalse(game.get(6, 6) instanceof Wall);
		assertTrue(game.get(0, 3) instanceof Wall);
		
	}
	@Test
	public void testDoor() throws Exception{
		setUp();
		assertTrue(game.getDoor() instanceof Door);	
	}

}
