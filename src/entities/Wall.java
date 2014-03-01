/* Author:	Zachary Stoner
 * File:	Wall.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Wall information.
 */
package entities;

public class Wall extends Entity {

	//Constructors
	public Wall(){};
	public Wall(int x, int y){ super(x,y);}
	
	//Methods
	public void draw(){
		//Draw Itself
		System.out.println("Drawing a Wall at " + pos());
	}
}
