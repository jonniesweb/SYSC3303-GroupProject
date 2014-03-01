package clientLogic;

import gameLogic.GameBoard;

public class ClientGUI {

	public void update(GameBoard game){
		System.out.println("The Client GUI is updating");
		//Do the updating
		game.draw();
		
		System.out.println("The Client GUI has finished updating");
	}
	
}
