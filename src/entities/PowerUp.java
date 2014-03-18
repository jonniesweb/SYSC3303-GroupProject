/* Author:	Zachary Stoner
 * File:	PowerUp.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides PowerUp information.
 */
package entities;

public class PowerUp extends Entity {

	//Depending on how many different types of power ups are created
	// It might be beneficial to use an enumeration to define which one
	// It is. 
	
	//Constructor
	public PowerUp(){};
	public PowerUp(int x, int y){
		super(x,y);
	}
}
