/* Author:	Zachary Stoner
 * File:	Bomb.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Bomb information.
 */
package entities;

public class Bomb extends Entity {

	private long creationTime;
	private long bombTimer;
	
	//Constructors
	public Bomb(){};
	public Bomb(int x, int y, long time){
		super(x,y);
		this.creationTime = System.currentTimeMillis();
		this.bombTimer = time;
	}
	
	//Methods
	public void hasExploded(){
		//Check time and compare
		if (System.currentTimeMillis() > (creationTime + bombTimer)){
			System.out.println("The Bomb has exploded");
		} else {
			System.out.println("The Bomb has yet to explode");
		}
	}
	
	public void draw(){
		//Draws Itself
		System.out.println("Drawing a Bomb at " + pos());
	}
}
