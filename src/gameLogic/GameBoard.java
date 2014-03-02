package gameLogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import entities.Bomb;
import entities.Door;
import entities.Enemy;
import entities.Entity;
import entities.Explosion;
import entities.Player;
import entities.PowerUp;
import entities.Wall;

public class GameBoard implements Runnable {

	// use these enums!
	public enum TILES {
		PLAYER, WALL, DOOR, BOMB, EXPLOSION, POWERUP, ENEMY, FLOOR
	};

	private Entity[][] board;
	private int width;
	private int height;

	Entity[] entitiesArray;

	public GameBoard() {
		this(3, 3);
		// Testing
		board = new Entity[][] { { new Entity(), new Player(), new Wall() },
				{ new Door(), new Bomb(), new Explosion() },
				{ new PowerUp(), new Enemy(), new Entity() } };
	}

	public GameBoard(int sizeX, int sizeY) {
		this.board = new Entity[sizeX][sizeY];
		this.width = sizeX;
		this.height = sizeY;

		// initialize TILES array
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = new Entity();
			}
		}

		entitiesArray = new Entity[8];
		entitiesArray[0] = new Player();
		entitiesArray[1] = new Wall();
		entitiesArray[2] = new Door();
		entitiesArray[3] = new Bomb();
		entitiesArray[4] = new Explosion();
		entitiesArray[5] = new PowerUp();
		entitiesArray[6] = new Enemy();
		entitiesArray[7] = new Entity();
	}

	public Entity[][] getBoard() {
		return board;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void run() {
	}

	public void generateFloor(String filename) {

		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Entity getEntityAt(int x, int y) {
		return board[x][y];
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				if (board[i][j] instanceof Wall)
					s += "W";
				else
					s += ".";
			}
			s += "\n";
		}
		return s;
	}
}
