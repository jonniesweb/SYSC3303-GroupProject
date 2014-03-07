package clientLogic;

import gameLogic.GameBoard;

/**
 * Main GUI class. This class should be the Facade in the Facade design pattern.
 * 
 * @author jonsimpson
 * 
 */
public class ClientGUI {

	ClientGUIFrame guiFrame;

	public ClientGUI(GameBoard gameBoard) {

		guiFrame = new ClientGUIFrame(gameBoard);
		guiFrame.setVisible(true);

	}

	public void update(GameBoard gameBoard) {
		System.out.println("The Client GUI is updating");

		guiFrame.update(gameBoard);

		System.out.println("The Client GUI has finished updating");
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
	
	public static void main(String[] args) {
		GameBoard board = new GameBoard(10, 10);
		board.randomizeFloor(2);
		ClientGUI gui = new ClientGUI(board);
		new Thread(gui.testUpdater).start();
		
		
	}
}
