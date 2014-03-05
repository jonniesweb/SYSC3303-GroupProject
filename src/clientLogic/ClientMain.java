package clientLogic;

import testing.TestDriver;

public class ClientMain {

	public static void main(String[] args) {
		System.out.println("The Client is Running");

		new Thread(new TestDriver("testNumber1.txt")).start();

		// Loop while running
		// Establish Connection
		// Loop
		// Update Gameboard
		// Check player life status
		// Send command
		// Kill Connection

	}

}
