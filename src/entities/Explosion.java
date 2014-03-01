/* Author:	Zachary Stoner
 * File:	Explosion.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Explosion information.
 */
package entities;

public class Explosion extends Entity {

	//Constructors
	public Explosion(){};
	public Explosion(int x, int y){
		super(x,y);
	}
	
	//Methods
	public void draw(){
		//Draw itself
		System.out.println("Drawing the Explosion at " + pos());
	}
}
