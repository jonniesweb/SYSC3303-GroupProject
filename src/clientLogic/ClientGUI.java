package clientLogic;

import org.apache.log4j.Logger;

import Networking.Network;
import gameLogic.GameBoard;

/**
 * Main GUI class. This class should be the Facade in the Facade design pattern.
 * 
 * @author jonsimpson
 * 
 */
public class ClientGUI {

	ClientGUIFrame guiFrame;
	private static final Logger LOG = Logger.getLogger(
			ClientGUI.class.getName());

	public ClientGUI() {

		guiFrame = new ClientGUIFrame();
		guiFrame.setVisible(true);

	}

	public void update(GameBoard gameBoard) {
		//System.out.println("The Client GUI is updating");
		LOG.info("The Client GUI is updating");

		guiFrame.update(gameBoard);

		//System.out.println("The Client GUI has finished updating");
		LOG.info("The Client GUI has finished updating");
	}
	
	private Runnable testUpdater = new Runnable() {
		public void run() {
			while (true) {
				guiFrame.update(ClientGUIFrame.testData());
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
//	public static void main(String[] args) {
//		GameBoard board = new GameBoard(10, 10);
//		board.randomizeFloor(2);
//		ClientGUI gui = new ClientGUI(board);
//		new Thread(gui.testUpdater).start();
//		
//		
//	}
}
