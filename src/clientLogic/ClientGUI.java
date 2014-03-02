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

	}

	public void update(GameBoard gameBoard) {
		System.out.println("The Client GUI is updating");

		guiFrame.update(gameBoard);

		System.out.println("The Client GUI has finished updating");
	}

}
