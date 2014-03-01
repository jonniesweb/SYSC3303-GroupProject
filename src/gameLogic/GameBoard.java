package gameLogic;

import entities.*;


public class GameBoard {

	// use these enums!
	public enum TILES {
		PLAYER, WALL, DOOR, BOMB, EXPLOSION, POWERUP, ENEMY, FLOOR
	};

	private TILES[][] board;
	private int width;
	private int height;

	Entity[] entitiesArray;

	public GameBoard() {
		this(3, 3);
		// Testing
		board = new TILES[][] { { TILES.FLOOR, TILES.PLAYER, TILES.WALL },
				{ TILES.DOOR, TILES.BOMB, TILES.EXPLOSION },
				{ TILES.POWERUP, TILES.ENEMY, TILES.FLOOR } };
	}

	public GameBoard(int sizeX, int sizeY) {
		this.board = new TILES[sizeX][sizeY];
		this.width = sizeX;
		this.height = sizeY;

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

	public TILES[][] getBoard() {
		return board;
	}

	public void draw() {
		int entityType;
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				if (board[k][i] == TILES.PLAYER) {
					entityType = 0;
				} else if (board[k][i] == TILES.WALL) {
					entityType = 1;
				} else if (board[k][i] == TILES.DOOR) {
					entityType = 2;
				} else if (board[k][i] == TILES.BOMB) {
					entityType = 3;
				} else if (board[k][i] == TILES.EXPLOSION) {
					entityType = 4;
				} else if (board[k][i] == TILES.POWERUP) {
					entityType = 5;
				} else if (board[k][i] == TILES.ENEMY) {
					entityType = 6;
				} else {
					entityType = 7;
				} // floor tile

				entitiesArray[entityType].setPos(k, i);
				entitiesArray[entityType].draw();
			}
		}
	}
}
