package serverLogic;

import java.util.*;
import java.util.Collections.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import entities.Enemy;
import gameLogic.GameBoard;


public class EnemyManager implements Runnable {
	
	//private GameBoard board;
	private LogicManager logicManager;
	private List<Enemy> enemyList;
	private Random rand = new Random();

	
	
	public EnemyManager(){
		
	}
	public EnemyManager(LogicManager logicManager){
		this.logicManager = logicManager;
		//this.board = board;
		enemyList = Collections.synchronizedList(new ArrayList<Enemy>());
		enemyList.add(new Enemy(0,3));
		enemyList.add(new Enemy(2,4));
	}
	public boolean removeEnemy(int x,int y){
		boolean r = false;
		for(int i = 0; i< enemyList.size(); i++){
			if(enemyList.get(i).getPosX() == x && enemyList.get(i).getPosY() == y){
				enemyList.get(i).loseLife();
				r = true;
			}
		}
		return r;
	}
	public Enemy[] returnEnemies(){
		ArrayList<Enemy> returnedEnemies = new ArrayList<Enemy>();
		for(int i = 0; i < enemyList.size(); i++)
			if(enemyList.get(i).isAlive())
				returnedEnemies.add(enemyList.get(i));
		return returnedEnemies.toArray( new Enemy[0]);
	}
	public boolean checkEnemy(int x, int y){
		for(int i =0; i< enemyList.size(); i ++){
			if(enemyList.get(i).getPosX() == x && enemyList.get(i).getPosY() == y)
				return true;
		}	
		return false;
	}
	@Override
	public void run() {
		int  r; 
		while(logicManager.getGameInProgress()){
			for(Enemy e : enemyList){
				//board.remove(e.getPosX(), e.getPosY());
				if(e.isAlive()){
					r = rand.nextInt(4) + 1;
					switch(r){
						case 1 :
							if(logicManager.validMove(e.getPosX(), e.getPosY() - 1))
								e.moveUp();
							else continue;
							break;
						case 2 :
							if(logicManager.validMove(e.getPosX(), e.getPosY() + 1))
								e.moveDown();
							else continue;
							break;
						case 3 :
							if(logicManager.validMove(e.getPosX() + 1, e.getPosY()))
								e.moveRight();
							else continue;
							break;
						case 4 :
							if(logicManager.validMove(e.getPosX() - 1, e.getPosY()))
								e.moveLeft();
							else continue;
							break;
						default :
							;
					}
					if(logicManager.checkExplosion(e.getPosX(), e.getPosY()))
						e.loseLife();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
