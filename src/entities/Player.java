package entities;


/**
 * 
 * @author zachstoner
 *
 */
public class Player extends Entity {

	private int lives;
	private String name;
	
	//Constructors
	
	/**
	 * 
	 */
	public Player(){
		this.lives = 1;
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @param name
	 */
	public Player(int x, int y, String name){
		super(x,y);
		this.lives = 1;
		this.name = name;
	}
	
	//Methods
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public void moveUp(){
		setPosY(--this.posY);
	}
	
	public void moveDown(){
		setPosY(++this.posY);
	}
	
	public void moveLeft(){
		setPosX(--this.posX);
	}
	
	public void moveRight(){
		setPosX(++this.posX);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isAlive(){ 
		if (lives >=  1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 */
	public void loseLife(){ lives--;}

	/**
	 * 
	 * @param inc
	 */
	public void incresaseLife(int inc){ lives+=inc;}	
	
	/**
	 * 
	 */
	public void resetLife(){ lives = 1;}
}
