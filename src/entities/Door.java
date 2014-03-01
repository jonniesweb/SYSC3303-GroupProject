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
	
	public void draw(){
		//Draw Itself
		if(this.visibility){System.out.print("Drawing the Door - Visible at ");}
		else{System.out.print("Drawing the Door - Invisible at ");}
		System.out.println(pos());
	}
}
