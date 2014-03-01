/* Author:	Zachary Stoner
 * File:	Player.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Player information.
 */
package entities;

public class Player extends Entity {

	private int lives;
	
	//Constructors
	//Initialize the player with one life.
	public Player(){
		this.lives = 1;
	}
	public Player(int x, int y){
		super(x,y);
		this.lives = 1;
	}
	
	//Methods
	
	//Is the player alive? Not really useful if
	//We don't make use of increaseLife(int)
	public boolean isAlive(){ 
		if (lives < 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public void loseLife(){ lives--;}

	//Unless we have a life increase power up this isn't necessary.
	public void incresaseLife(int inc){ lives+=inc;}	
	
	public void resetLife(){ lives = 1;}
	
	public void draw(){
		//Draws Itself
		System.out.println("Drawing a Player at " + pos());
	}	
}
