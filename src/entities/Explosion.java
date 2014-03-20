/* Author:	Zachary Stoner
 * File:	Explosion.java
 * Description:
 * 	An Entity from the Bomberman project. Inherits from the Entity Class
 * 	Provides Explosion information.
 */
package entities;

public class Explosion extends Entity {

	private long create;
	private long blow;
	public Explosion(){};
	public Explosion(int x, int y,long created, long blowUp){
		super(x,y);
		this.create = created;
		this.blow = blowUp;
	}
	public Explosion(int x, int y){
		super(x,y);
	}
	
	public boolean isDone(long time){
		return (time > blow);
	}
}
