package serverLogic;

import java.util.ArrayList;
import java.util.Random;

import entities.Enemy;
import gameLogic.GameBoard;


public class EnemyManager implements Runnable {
	
	//private GameBoard board;
	private LogicManager logicManager;
	private ArrayList<Enemy> enemyList;
	private Random rand = new Random();

	
	
	public EnemyManager(){
		
	}
	public EnemyManager(LogicManager logicManager){
		this.logicManager = logicManager;
		//this.board = board;
		enemyList = new ArrayList<Enemy>();
		enemyList.add(new Enemy(0,3));
		 
		 logicManager.placeEnemy(enemyList);
	}

	@Override
	public void run() {
		int  r; 
		while(logicManager.getGameInProgress()){
			for(Enemy e : enemyList){
				//board.remove(e.getPosX(), e.getPosY());
				logicManager.removeEnemyFromGameBoard(e);
				r = rand.nextInt(4) + 1;
				switch(r){
					case 1 :
						if(logicManager.validMove(e.getPosX(), e.getPosY() + 1))
							e.moveUp();
						break;
					case 2 :
						if(logicManager.validMove(e.getPosX(), e.getPosY() - 1))
							e.moveDown();
						break;
					case 3 :
						if(logicManager.validMove(e.getPosX() + 1, e.getPosY()))
							e.moveRight();
						break;
					case 4 :
						if(logicManager.validMove(e.getPosX() - 1, e.getPosY()))
							e.moveLeft();
						break;
					default :
					;
				}
				logicManager.setEnemy(e);

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
		}
	}

}
