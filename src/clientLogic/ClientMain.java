package clientLogic;

import gameLogic.GameBoard;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

import Networking.Message;
import Networking.Network;
import testing.TestDriver;

public class ClientMain {
	
	private Network network;
	private Semaphore inboxLock = new Semaphore(0);
	private boolean running = false;
	private ClientGUI view;
	

	public static void main(String[] args) {
		System.out.println("The Client is Running");

//		GameBoard board = new GameBoard(5, 5);
//		System.out.println();
		
		GameBoard game = new GameBoard(5, 5);
		char[] charBoard = game.toString().toCharArray();
		byte[] byteBoard = new byte[charBoard.length];
		
		for (int i = 0; i < charBoard.length; i++) {
			byteBoard[i] = (byte) charBoard[i];
		}
		
		for (int i = 0; i < byteBoard.length; i++) {
			System.out.print((char) byteBoard[i]);
		}
		GameBoard newBoard = new GameBoard(new String(byteBoard).toCharArray());
		
		System.out.println(game);
		System.out.println(newBoard);
		
//		new ClientMain();
		// new Thread(new TestDriver("testNumber1.txt")).start();

		// Loop while running
		// Establish Connection
		// Loop
		// Update Gameboard
		// Check player life status
		// Send command
		// Kill Connection

	}
	
	

	public ClientMain() {
		ClientGUI view = new ClientGUI();
		// setup network
		network = new Network(Network.CLIENT_PORT_NO, inboxLock);
		new Thread(network).start();
		view = new ClientGUI();
		
		// connect to server
		String connectCommand = "SPECTATE_GAME";
		int port = Network.SEVER_PORT_NO;
		long time = System.nanoTime();
		String ip = "127.0.0.1";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		startListening();
		
		network.sendMessage(new Message(connectCommand, ip, port, time));
		
		
	}
	
	/**
	 * listen for incoming data
	 */
	private void startListening() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				running = true;
				Message message;
				
				while(running) {
					
					message = readInbox();
					
					byte[] data = message.datagram.getData();
					String gameString = new String(data);
					GameBoard b = new GameBoard(gameString.toCharArray());
					view.update(b);
					
					
				}
			}
		});
	}



	private Message readInbox() {
		try {
			inboxLock.acquire();
		} catch (InterruptedException e) {
			System.out.println("Thread Interrupted returning empty message");
			return null;
		}
		return network.getMessage();
	}
}

