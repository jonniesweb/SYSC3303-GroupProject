package clientLogic;

import gameLogic.*;
import testing.TestDriver;



public class ClientMain {
	
	private static final int SIZE_X = 3;
	private static final int SIZE_Y = 3;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("The Client is Running");


		new Thread(new TestDriver("testNumber1.txt")).start();
		
		//	Loop while running
		//		Establish Connection
		// 		Loop
		//			Update Gameboard
		//			Check player life status
		//			Send command
		//		Kill Connection

	}

}
