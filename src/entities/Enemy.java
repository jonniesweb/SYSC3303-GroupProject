/* Author:	Zachary Stoner
 * File:	Enemy.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Enemy information.
 */
package entities;

public class Enemy extends Entity {
	int lives;
	//Constructor
	public Enemy(){this.lives = 1;};
	
	public Enemy(int x, int y){
		super(x,y);
		this.lives = 1;
	}
	/**
	 * 
	 * @return
	 */
	
	public void moveUp(){
		setPosY(--this.posY);
	}
	
	public void moveDown(){
		setPosY(++this.posY);
	}
	
	public void moveLeft(){
		setPosX(--this.posX);
	}
	
	public void moveRight(){
		setPosX(++this.posX);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isAlive(){ 
		if (lives >=  1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 */
	public void loseLife(){ lives--;}

	/**
	 * 
	 * @param inc
	 */
	public void incresaseLife(int inc){ lives+=inc;}	
	
	/**
	 * 
	 */
	public void resetLife(){ lives = 1;}
}
