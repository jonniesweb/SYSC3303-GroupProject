/* Author:	Zachary Stoner
 * File:	Enemy.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Enemy information.
 */
package entities;

public class Enemy extends Entity {

	//Constructor
	public Enemy(){};
	public Enemy(int x, int y){
		super(x,y);
	}
	
	//Methods
	public void draw(){
		System.out.println("Drawing an Enemy at " + pos());
	}
}
