package gameLogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Logger;



import entities.*;

//import entities.Bomb;
//import entities.Explosion;
import entities.PowerUp;

//import serverLogic.LogicManager;
//import serverLogic.UserManager;
//import serverLogic.User;


//TODO: gameboard should be init with a list of players
public class GameBoard {

	// use these enums!
	public enum TILES {
		PLAYER, WALL, DOOR, BOMB, EXPLOSION, POWERUP, ENEMY, FLOOR
	};

	private Entity[][] board;
	private int width;
	private int height;
	
	private int playerCount;

	private static final Logger LOG = Logger.getLogger(GameBoard.class
			.getName());

	// UserManager userManager;

	/**
	 * 
	 * 
	 */
	public GameBoard(int width, int height) {
		this.width = width;
		this.height = height;
		board = new Entity[height][width];

		//this.randomizeFloor(4);
		//this.generateFloor("FloorTest.txt");
		//this.initializeDoor();

		this.playerCount = 1;
	}
	
	/**
	 * 
	 * 
	 * @param filename
	 */
	public GameBoard(String filename) {
		this.playerCount = 1;
		generateFloor(filename);

	}
	
	/**
	 * 
	 * @param filename
	 * @param players
	 */
	public GameBoard(String filename, int players){
		generateFloor(filename, players);
	}

	/**
	 * Creates a GameBoard from a serialized byte array
	 * 
	 * @param serializedGameBoard
	 */
	public GameBoard(char[] serializedGameBoard) {

		this.playerCount = 0;
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

		// System.out.println("gameboard: height:"+ height +" width: "+width);

		board = new Entity[width][height];
		LOG.info("Gameboard: height:" + height + " width: " + width);
		/*
		 * Parse serializedGameBoard to create the board
		 */
		int x = 0;
		int y = 0;
		char entity;

		for (int i = 0; i < serializedGameBoard.length; i++) {

			entity = (char) serializedGameBoard[i];

			if (entity == 'W') {
				set(new Wall(x, y));
			} else if (entity == 'P') {
				playerCount++;
				set(new Player(x, y, ""));
			} else if (entity == 'D') {
				set(new Door(x, y));
			}else if(entity == 'O'){
				set(new Enemy(x,y));
			}else if(entity == 'E'){
				set(new Explosion(x,y));
			}else if(entity == 'B'){
				set(new Bomb(x,y));
			} else if(entity == 'U') {
				set(new PowerUp(x, y));
			} else if (entity == '\n') {
				y++;
				x = -1;
			} else {
				set(new Entity(x, y));
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

	public boolean hasDoor(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return false;
		return board[x][y] instanceof Door;
	}

	public boolean hasEnemy(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return false;
		return board[x][y] instanceof Enemy;
	}
	
	public boolean hasPowerUp(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return false;
		return board[x][y] instanceof PowerUp;
	}

	
	/**
	 * Initialize the playerCount
	 * @param p
	 */
	public void setPlayerCount(int players){
		this.playerCount = players;
	}
	
	public int getPlayerCount(){
		return this.playerCount;
	}
	
	/**
	 * Generate floor from prescribed file
	 * @param filename
	 * @param players
	 */
	public void generateFloor(String filename, int players){
		setPlayerCount(players);
		generateFloor(filename);
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
						board[j][i] = new Wall(j, i);
					else if (line.charAt(j) == '.')
						board[j][i] = new Entity(j, i);
					else if (line.charAt(j) == 'D')
						board[j][i] = new Door(j, i);
				}
				i++;

			}
			initializeDoor();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		placePowerUps();
		
	}

	
	
	/**
	 * Initialize with random position of wall. Takes number of player so that
	 * the player will not be surrounded by wall
	 * 
	 * @param players
	 */
	public void randomizeFloor(int players){
		
		setPlayerCount(players);
		
		Random r = new Random();
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//top left and bot right should have space for player to move
				//if the position is top left(Player 1 position),skip randomization
				if( (i == 0 && j == 0) || (i == 0 && j == 1)||(i == 1 && j == 0)){
					board[i][j] = new Entity();
					continue;
				}
				if(playerCount >1  && ((i == height-1 && j == width-1) || (i == height-1&&j == width-2)||(i == height-2 && j == width-1)) ){
					board[i][j] = new Entity();
					continue;
				}
				if(playerCount >2 && ((i==height-2 && j==0) || (i==height-1&&j==0)||(i==height-1&&j==1)) ){
					board[i][j] = new Entity();
					continue;
				}
				if(playerCount >3 && ((i==0 && j == width-1) || (i == 0 && j == width-2)||(i == 1 && j == width-1)) ){
					board[i][j] = new Entity();
					continue;
				}
				
				if (r.nextInt(10) < 7) {
					board[i][j] = new Wall();
				} else {
					board[i][j] = new Entity();
				}
			}
		}
		
		initializeDoor();
		
		placePowerUps();
	}

	
	public void placePowerUps(){
		int powerUpX, powerUpY;
		Random r = new Random();
		
		for (int i = 0; i < playerCount; i++){
			do{
				powerUpX = r.nextInt(width-2) + 1;
				powerUpY = r.nextInt(height-2) + 1;	
				
			}while(((board[powerUpX][powerUpY] instanceof PowerUp) || (board[powerUpX][powerUpY] instanceof Door)));
		
			//TODO make powerup either randomize itself or specify a type here in construction
			board[powerUpX][powerUpY] = new PowerUp(powerUpX,powerUpY);
			System.out.println("PowerUp Placed at: ("+powerUpX+","+powerUpY+")");
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
		if (x < 0 || x >= width)
			return new Wall();
		if (y < 0 || y >= height)
			return new Wall();
		return board[x][y];
	}

	/**
	 * Place an entity on the gameboard at the given x and y position.
	 * 
	 * @deprecated Use set(Entity) instead
	 * @param entity
	 * @param x
	 * @param y
	 * @return
	 */
	public Entity set(Entity entity, int x, int y) { // TODO: remove x and y
														// parameters. Use
														// entity.getx() and
														// entity.getY() instead
		if (x < 0 || x > width) {
			LOG.error("X IS OUT OF BOUND : " + x);
		}
		if (y < 0 || y > height) {
			LOG.error("Y IS OUT OF BOUND : " + y);
		}

		Entity previousEntity = board[x][y];
		board[x][y] = entity;
		return previousEntity;
	}

	/**
	 * Places an entity on the gameboard at the x,y position specified in
	 * itself.
	 * 
	 * @param entity
	 * @return
	 */
	public Entity set(Entity entity) {
		int x = entity.getPosX();
		int y = entity.getPosY();
		Entity previousEntity = board[x][y];
		board[x][y] = entity;
		return previousEntity;
	}
	
	/**
	 * Remove entity at specified position
	 * 
	 * @param x
	 * @param y
	 */
	public Entity remove(int x, int y) {
		Entity previous = board[x][y];
		board[x][y] = new Entity(x, y);
		return previous;
	}

	/**
	 * Initialize door into the board If there is door already e.g door
	 * initialized inside prescribed file
	 * 
	 */

	public void initializeDoor(){
		
		for(int i = 0;i<height;i++){
			for(int j = 0;j<height;j++){
				if(board[i][j] instanceof Door)
					return;
			}
		}


		board[3][3] = new Door(3, 3);
	}

	/**
	 * for testing
	 * 
	 * @return
	 */
	public Door getDoor() {

		return (Door) board[3][3];
	}
	private boolean checkBomb(int x, int y, Bomb[] b ){
		for(int i=0; i<b.length; i++ )
			if(b[i].getPosX() == x && b[i].getPosY() == y)
				return true;
		return false;
	}
	private boolean checkExplos(int x, int y, Explosion[] b ){
		for(int i=0; i<b.length; i++ )
			if(b[i].getPosX() == x && b[i].getPosY() == y)
				return true;
		return false;
	}
	private boolean checkEnemy(int x, int y, Enemy[] b ){
		for(int i=0; i<b.length; i++ )
			if(b[i].getPosX() == x && b[i].getPosY() == y)
				return true;
		return false;
	}
	/**
	 * For testing purposes eg. log into file the board view in string
	 */

	public String toString(Bomb[] bombs, Explosion[] explos, Enemy[] enemies) {
		LOG.info("bombslength: "+bombs.length+" explosion length:"+explos.length);
		String s = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if(checkExplos(x,y,explos) && !(board[x][y] instanceof Player))
					s+="E";
				else if(checkBomb(x,y,bombs) && !(board[x][y] instanceof Player))
					s+="B";
				else if(checkEnemy(x,y, enemies)&& !(board[x][y] instanceof Player))
					s+="O";
				else if (board[x][y] instanceof Wall)
					s += "W";
				else if (board[x][y] instanceof Player)
					s += "P";
				else if (board[x][y] instanceof Door)
					s += "D";
				else if (board[x][y] instanceof PowerUp)
					s += "U";
				else
					s += ".";
			}
			s += "\n";
		}
		return s;
	}
	
	/**
	 * For testing purposes eg. log into file the board view in string
	 */

	public String toString() {
		
		String s = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (board[x][y] instanceof Wall)
					s += "W";
				else if (board[x][y] instanceof Player)
					s += "P";
				else if (board[x][y] instanceof Door)
					s += "D";
				else if (board[x][y] instanceof PowerUp)
					s += "U";
				else
					s += ".";
			}
			s += "\n";
		}
		return s;
	}
}
