package serverLogic;
import  gameLogic.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import entities.Entity;
import entities.Bomb;
import entities.Explosion;
import java.util.TimerTask;
import Networking.*;

/* Bomb factory keeps track of all explosions/bombs
 * Bomb factory is responsible for all bomb/explosion timing
 * 
 * 
 */
public class BombFactory {
	private final int blowUp = 2000;
	private final int explode = 2000;
	private final int MAX_BOMBS = 1;
	// number of squares in a given direction to 
	// add explosions to
	
	private Map<User,Integer> bombCounter;
	private Map<Bomb, User>	  bombMapper;
	private List<Explosion>  explosions;
	private List<Bomb> 	  bombs;
	private LogicManager logicManager;
	private int width, height;
	// constructor inits maps
	// maps each user to max bomb number
	public BombFactory(Object[] users,  int w, int h,LogicManager m){
		logicManager = m;
		width = w; height = h;
		bombCounter = Collections.synchronizedMap(new HashMap<User,Integer>());
		bombMapper = Collections.synchronizedMap(new HashMap<Bomb,User>());
		explosions = Collections.synchronizedList(new ArrayList<Explosion>());
		bombs 	= Collections.synchronizedList(new ArrayList<Bomb>());
		for(int i = 0; i< users.length; i++){
			User u = (User)users[i];
			bombCounter.put(u, new Integer(MAX_BOMBS));	
		}
		
	}
	
	// creates a bomb at users xy 
	// adds that bomb to list of all bombs 
	// and maps that bomb to a user
	public boolean startBomb(User u){
		if(bombCounter.containsKey(u) &&(bombCounter.get(u) >0 )){
			bombCounter.put(u, bombCounter.get(u)-1);
			long createdTime = System.currentTimeMillis();
			long blowUpTime = createdTime + blowUp;
			Bomb b = new Bomb(u.getPlayer().getPosX(),
					u.getPlayer().getPosY(),
					createdTime,blowUpTime);
			bombs.add(b);
			bombMapper.put(b,u);
			timerExecute();
			return true;
		}else{
			return false;
		}
	}

	// creates explosions at the specified squares
	// length of row of explosions determined by EXP_NUM
	// checks if square is within range
	public void createExplosions(Bomb b, boolean powerUp){
		int EXP_NUM = 1;
		if(powerUp)
			EXP_NUM = 2;
		long time = System.currentTimeMillis();
		for(int i =0; i < EXP_NUM; i++){
			if( b.getPosY()-1-i >= 0){
				explosions.add(new Explosion(b.getPosX(),
					b.getPosY()-1-i,
					time, time+explode));
				logicManager.execute(new BombMessage("EXP_ADDED",b.getPosX(),b.getPosY()-1-i));
			}
		}
		// bottom
		for(int i = 0; i< EXP_NUM; i++){
			if(b.getPosY()+i+ 1 < height){
				explosions.add(new Explosion(b.getPosX(),
					b.getPosY()+1+i,
					time, time+explode));
				logicManager.execute(new BombMessage("EXP_ADDED",b.getPosX(),b.getPosY()+1+i));
			}
		}
		// left
		for(int i =0; i < EXP_NUM; i++ ){
			if(b.getPosX()-i -1 >= 0){
				explosions.add(new Explosion(b.getPosX()-1-i,
					b.getPosY(),
					time, time+explode));
				logicManager.execute(new BombMessage("EXP_ADDED",b.getPosX()-1-i,b.getPosY()));
			}
		}
		//right
		for(int i=0; i< EXP_NUM;i++){
			if(b.getPosX()+i + 1 < width){
				explosions.add(new Explosion(b.getPosX()+1+i,
					b.getPosY(),
					time, time+explode));
				logicManager.execute(new BombMessage("EXP_ADDED",b.getPosX()+1+i,b.getPosY()));
			}
		}
		// add bomb on myself
		explosions.add(new Explosion(b.getPosX(),b.getPosY(),time,time+explode));
		logicManager.execute(new BombMessage("EXP_ADDED",b.getPosX(),b.getPosY()));
		
	}
	
	
	// for each bomb that has exploded
	// create an explosion
	// delete the bomb->user mapping
	// inc the users bombCounter
	// remove the bomb from list of all bombs
	private void blowUpBombs(){
		long time = System.currentTimeMillis();
		for(int i =0; i < bombs.size(); i++){
			if(bombs.get(i).hasExploded(time)){
				Bomb b = bombs.remove(i);
				User u = bombMapper.get(b);
				bombMapper.remove(b);
				bombCounter.put(u, bombCounter.get(u)+1);
				createExplosions(b, u.getPlayer().getBombRangePowerUpEnabled());
			}
		}
	}
	// remove any explosions that have stayed past their welcome
	private void removeExplosions(){
		Entity r;
		long time = System.currentTimeMillis();
		int length = explosions.size();
		while(!explosions.isEmpty() ){
			if(explosions.get(explosions.size()-1).isDone(time)){
				r = explosions.remove(explosions.size()-1);
				logicManager.execute( new BombMessage("EXP_REMOVED",r.getPosX(),r.getPosY()));
			}else{
				System.out.println("Explosion isnt done for some reason....");
			}
		}
	}
	public Bomb[] returnBombs(){
		return bombs.toArray(new Bomb[0]);
	}
	public Explosion[] returnExplosions(){
		return (explosions.toArray(new Explosion[0]));
	}
	
	public void timerExecute(){
		TimerTask t1 = new TimerTask(){
			public void run(){
				blowUpBombs();
			}};
		TimerTask t2 = new TimerTask(){
				public void run(){
					removeExplosions();
				}};
		// 100 delay to make sure bomb exploded
		new Timer().schedule(t1, blowUp+100);
		new Timer().schedule(t2, blowUp+explode+ 1000);
	}
	
}