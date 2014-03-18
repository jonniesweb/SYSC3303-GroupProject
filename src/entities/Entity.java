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
	
	protected int posX; //X position of the Entity
	protected int posY; //Y position of the Entity
	
	
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
	
	public void setPosX(int x){
		posX = x;
	}
	public void setPosY(int y){
		posY = y;
	}
	public void setPos(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public int getPosX(){ return posX;}
	public int getPosY(){ return posY;}
	
	public String getPos(){
		return "("+posX+","+posY+")";
	}
}
