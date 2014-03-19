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
	public Bomb(int x, int y, long time,long blowUp){
		super(x,y);
		this.creationTime = time;
		this.bombTimer = blowUp;
	}
	
	//Methods
	public boolean hasExploded(long time){
		return (time > bombTimer);
	}
}
