package gameLogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import entities.Door;
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
	public GameBoard(int width,int height){
		this.width = width;
		this.height = height;
		board = new Entity[height][width];
		this.randomizeFloor(4);
		//this.generateFloor("FloorTest.txt");
		this.initializeDoor();
	}
	
	/**
	 * 
	 * 
	 * @param filename
	 */
	public GameBoard(String filename){
		generateFloor(filename);	
		
	}
	
	/**
	 * Creates a GameBoard from a serialized byte array
	 * @param serializedGameBoard
	 */
	public GameBoard(char[] serializedGameBoard) {
		
		this.width = 0;
		this.height = 0;
		int totalBytes = serializedGameBoard.length;
		
		// get the width of the gameboard
		for (int i = 0; i < serializedGameBoard.length; i++) {
			if (serializedGameBoard[i] == '\n') {
				width = i;
				break;
			}
		}
		
		// determine height
		try {
			height = totalBytes / (width + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// create GameBoard with found out width and height
		board = new Entity[height][width];
		
		/*
		 * Parse serializedGameBoard to create the board
		 */
		int x = 0;
		int y = 0;
		char entity;
		
		for (int i = 0; i < serializedGameBoard.length; i++) {
			
			entity = (char) serializedGameBoard[i];
			
			if (entity == 'W') {
				set(new Wall(x, y), x, y);
			} else if (entity == 'P') {
				set(new Player(x, y, ""), x, y);
			} else if (entity == 'D') {
				set(new Door(x, y), x, y);
			} else if (entity == '\n') {
				y++;
				x = -1;
			} else {
				set(new Entity(x, y), x, y);
			}
			
			x++;
		}
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
				for (int j = 0; j < line.length(); j++) {
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
		if(x < 0 || x >= width )
			return new Wall();
		if(y< 0 || y >= height)
			return new Wall();
		return board[x][y];
	}
	
	public Entity set(Entity entity, int x, int y) {
		if(x < 0 || x > width)
			System.out.println("x out of bounds: "+ x);
		if(y < 0 || y > height)
			System.out.println("y out of bounds: "+ y);
		Entity previousEntity = board[x][y];
		board[x][y] = entity;
		return previousEntity;
	}
	/**
	 * Remove entity at specified position
	 * @param x
	 * @param y
	 */
	public void remove(int x, int y){
		board[x][y]= new Entity(x,y);
	}
	/**
	 * Initialize door into the board
	 * If there is door already e.g door initialized inside prescribed file
	 * 
	 */
	public void initializeDoor(){
		for(int i = 0;i<height;i++){
			for(int j = 0;j<height;j++){
				if(board[i][j] instanceof Door)
					return;
			}
		}
		board[4][4] = new Door(4,4);	
	}
	
	public Door getDoor(){
		return (Door)board[4][4];
	}
	/**
	 * For testing purposes
	 * eg. log into file the board view in string
	 */
	public String toString() {
		int playerCount = 0;
		String s = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (board[x][y] instanceof Wall)
					s += "W";
				else if (board[x][y] instanceof Player)
					s += "P";
				else if(board[x][y] instanceof Door)
					s += "D";
				else 
					s += ".";
			}
			s += "\n";
		}
		return s;
	}
}
