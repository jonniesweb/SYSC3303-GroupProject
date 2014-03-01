package gameLogic;

import entities.*;

public class GameBoard {
	
	// use these enums!
	public enum TILES {PLAYER, WALL, DOOR, BOMB, EXPLOSION, POWERUP, ENEMY, FLOOR};
	
	private char[][] board;
	private int width;
	private int height;
	
	Entity[] entitiesArray;
	
	public GameBoard(){
		this(3,3);
		 //Testing
		board = new char[][]
		   {{'7','0','1'},
			{'2','3','4'},
			{'5','6','1'}};	
	}
	public GameBoard(int sizeX, int sizeY){
		this.board = new char[sizeX][sizeY];
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
	
	
	public char[][] getBoard(){return board;}
	
	public void draw(){
		int entityType;
		for(int i = 0; i < height; i++){
			for (int k = 0; k < width; k++){
				if (board[k][i] == '0'){entityType = 0;}
				else if (board[k][i] == '1'){entityType = 1;}
				else if (board[k][i] == '2'){entityType = 2;}
				else if (board[k][i] == '3'){entityType = 3;}
				else if (board[k][i] == '4'){entityType = 4;}
				else if (board[k][i] == '5'){entityType = 5;}
				else if (board[k][i] == '6'){entityType = 6;}
				else {entityType = 7;}
				entitiesArray[entityType].setPos(k, i);
				entitiesArray[entityType].draw();
			}
		}
	}

}
