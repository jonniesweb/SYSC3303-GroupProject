package testing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import entities.Enemy;
import entities.Entity;
import entities.Player;
import entities.Door;
import entities.PowerUp;
import serverLogic.LogicManager;
import serverLogic.UserManager;
import gameLogic.GameBoard;

import java.util.ArrayList;
import java.util.Random;



public class TestEnemyExistance {
	
	UserManager u;
	LogicManager logic;
	GameBoard game;
	Entity[][] board;
	
	@Test
	public void testEnemyGeneration(){
		
		
		u = new UserManager();
		logic = new LogicManager(u);
		game = new GameBoard(7,7);
		game.randomizeFloor(2);
		logic.setGameBoard(game);
		board = game.getBoard();
		ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
		
		int posX, posY;
		
		Random r = new Random();
		
		//Generate 3 enemies
		for(int i = 0; i < 3; i++){
			do{
				posX = r.nextInt(game.getWidth());
				posY = r.nextInt(game.getHeight());
			} while (enemyNotPlaceable(posX, posY));
			
			enemyList.add(new Enemy(posX,posY));
			logic.placeEnemy(enemyList);
			
		}
		
		assertEquals(3, enemyCount());
		
	}
	
	private boolean enemyNotPlaceable(int x, int y){
		
		return ((board[x][y] instanceof Door) || (board[x][y] instanceof PowerUp) 
				|| (board[x][y] instanceof Player) || (board[x][y] instanceof Enemy));
		
	}
	
	private int enemyCount(){
		
		int count = 0;
		
		for(int i = 0; i < game.getWidth(); i++){
			for(int k = 0; k < game.getHeight(); k++){
				
				if(board[i][k] instanceof Enemy)
					count++;
			}
		}
		
		return count;
	}
	
	public static void main(String[] args) {
		System.out.println("Something something main");
      Result result = JUnitCore.runClasses(TestEnemyExistance.class);
      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
      System.out.println(result.wasSuccessful());
	}

}
