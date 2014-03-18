/* Author:	Zachary Stoner
 * File:	Door.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Door information.
 */
package entities;

public class Door extends Entity{
	
	private boolean visibility;
	
	//Constructors
	public Door(){
		this.visibility = false;
	}
	public Door(int x, int y){
		super(x,y);
		this.visibility = false;
	}
	
	//Methods
	
	public boolean isVisible(){return this.visibility;}
	
	public void makeVisible(){ this.visibility = true;}
}
