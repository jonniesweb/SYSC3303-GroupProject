package serverLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import entities.Enemy;

public class EnemyManager implements Runnable {

	// private GameBoard board;
	private LogicManager logicManager;
	private List<Enemy> enemyList;
	private Random rand = new Random();

	// setup logger
	private static final Logger LOG = Logger.getLogger(EnemyManager.class
			.getName());

	public EnemyManager() {

	}

	public EnemyManager(LogicManager logicManager) {
		this.logicManager = logicManager;
		// this.board = board;
		enemyList = Collections.synchronizedList(new ArrayList<Enemy>());
		enemyList.add(new Enemy(0,3));
		enemyList.add(new Enemy(2,4));
	}

	public boolean removeEnemy(int x, int y) {
		for (int i = 0; i < enemyList.size(); i++) {
			if(enemyList.get(i).getPosX() == x && enemyList.get(i).getPosY() == y){
				enemyList.get(i).loseLife();
				LOG.info("Enemy died at " + enemyList.get(i).getPos());
				return true;
			}
		}
		LOG.error("No enemy found at (" + x + "," + y + ")");
		return false;
	}

	public Enemy[] returnEnemies() {
		ArrayList<Enemy> returnedEnemies = new ArrayList<Enemy>();
		for (int i = 0; i < enemyList.size(); i++)
			if (enemyList.get(i).isAlive())
				returnedEnemies.add(enemyList.get(i));
		return returnedEnemies.toArray(new Enemy[0]);
	}

	public boolean checkEnemy(int x, int y) {
		for (int i = 0; i < enemyList.size(); i++) {
			if(enemyList.get(i).getPosX() == x && enemyList.get(i).getPosY() == y)
				return true;
		}
		return false;
	}

	@Override
	public void run() {

		int r;
		while (logicManager.getGameInProgress()) {
			for (Enemy e : enemyList) {
				// board.remove(e.getPosX(), e.getPosY());
				if (e.isAlive()) {
					r = rand.nextInt(4) + 1;
					switch (r) {
					case 1:
						//TODO this is why the enemies don't kill players
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
						break;
					}
					if (logicManager.checkExplosion(e.getPosX(), e.getPosY()))
						e.loseLife();
				}
				LOG.info("New enemy position " + e.getPos());
				logicManager.sendGameBoardToAll();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}
