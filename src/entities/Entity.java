/* Author: 	Zachary Stoner
 * File:	Entity.java
 * Description: 
 * 	The Entity base class. Provides a base for all entities utilized by 
 * 	the Bomberman game. 
 * 	Provides a method that allows for drawing of itself and acces to the 
 * 	position variables.
 */
package entities;

import java.lang.String;

public class Entity {
	
	private int posX; //X position of the Entity
	private int posY; //Y position of the Entity
	
	
	//Constructors
	public Entity(){
		posX = -1;
		posY = -1;
	};
	public Entity(int x, int y) {
		posX = x;
		posY = y;
	}
	
	//Methods
	
	public void setPos(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public int getPosX(){ return posX;}
	public int getPosY(){ return posY;}
	
	public String pos(){
		return "("+posX+","+posY+")";
	}
	
	/**
	 * @deprecated Let the GUI take the entities and draw them, not the Model
	 */
	public void draw(){
		// Draw itself
		System.out.println("Default Draw at " + pos());
	}
}
