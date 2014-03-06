package gameLogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import entities.Entity;
import entities.Player;
import entities.Wall;
//import entities.Bomb;
//import entities.Door;
//import entities.Enemy;
//import entities.Explosion;
//import entities.PowerUp;

import serverLogic.UserManager;
import serverLogic.User;


//TODO: gameboard should be init with a list of players
public class GameBoard {

	// use these enums!
	public enum TILES {
		PLAYER, WALL, DOOR, BOMB, EXPLOSION, POWERUP, ENEMY, FLOOR
	};

	private Entity[][] board;
	private int width;
	private int height;
	
	//UserManager userManager;

	/**
	 * 
	 * 
	 */
	public GameBoard(){
		
	}
	
	/**
	 * 
	 * 
	 * @param filename
	 */
	public GameBoard(String filename){
		generateFloor(filename);	
		placePlayers();
		
	}

	/**
	 * 
	 * @return
	 */
	public Entity[][] getBoard() {
		return board;
	}

	/**
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @deprecated Use the equivalent method from LogicManager since this class
	 *             shouldn't know about UserManager!
	 */ 
	public void placePlayers(){
		
		int i = 0;
		int x;
		int y;
		
		for(User u: userManager.getCurrentPlayerList()){
			if( i < 2){
				x = 0;
				y = (i%2)*height;
			} else {
				x = width;
				y = (i%2)*height;
			}
			i++;
			board[x][y] = u.getPlayer();
			u.getPlayer().setPos(x, y);
		}
		
	}
	
	/**
	 * Generate floor from prescribed file
	 * 
	 * @param filename
	 */
	public void generateFloor(String filename) {

		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(fileReader);

		String line = null;
		// if no more lines the readLine() returns null
		int i = 0;
		try {
			while ((line = br.readLine()) != null) {
				for (int j = 0; j < 7; j++) {
					if (line.charAt(j) == 'W')
						board[i][j] = new Wall(i, j);
					else if (line.charAt(j) == '.')
						board[i][j] = new Entity(i, j);
				}
				i++;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize with random position of wall. Takes number of player so that
	 * the player will not be surrounded by wall
	 * 
	 * @param playerCount
	 */
	public void randomizeFloor(int playerCount){
		
		
		Random r = new Random();
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//top left and bot right should have space for player to move
				//if the position is top left(Player 1 position),skip randomization
				if( (i == 0 && j == 0) || (i == 0 && j == 1)||(i == 1 && j == 0))
					continue;
				if(playerCount >1  && ((i == height-1 && j == width-1) || (i == height-1&&j == width-2)||(i == height-2 && j == width-1)) )
					continue;
				if(playerCount >2 && ((i==height-2 && j==0) || (i==height-1&&j==0)||(i==height-1&&j==1)) )
					continue;
				if(playerCount >3 && ((i==0 && j == width-1) || (i == 0 && j == width-2)||(i == 1 && j == width-1)) )
					continue;
				
				if (r.nextInt(10) < 7) {
					board[i][j] = new Wall();
				} else {
					board[i][j] = new Entity();
				}
			}
		}
	}
	
	/**
	 * Get entity
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Entity get(int x, int y) {
		return board[x][y];
	}
	
	public Entity set(Entity entity, int x, int y) {
		Entity previousEntity = board[x][y];
		board[x][y] = entity;
		return previousEntity;
	}
	/**
	 * For testing purposes
	 * eg. log into file the board view in string
	 */
	public String toString() {
		int playerCount = 0;
		String s = "";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (board[i][j] instanceof Wall)
					s += "W";
				else if (board[i][j] instanceof Player)
					s += playerCount++;
				else 
					s += ".";
			}
			s += "\n";
		}
		return s;
	}
}
