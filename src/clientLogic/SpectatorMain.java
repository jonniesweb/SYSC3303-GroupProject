package clientLogic;

import gameLogic.GameBoard;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

import Networking.Message;
import Networking.Network;
import testing.TestDriver;

public class SpectatorMain {
	
	protected Network network;
	private Semaphore inboxLock = new Semaphore(0);
	private boolean running = false;
	protected ClientGUI view;
	

	public static void main(String[] args) {
	
		
		new SpectatorMain();
		// new Thread(new TestDriver("testNumber1.txt")).start();

		// Loop while running
		// Establish Connection
		// Loop
		// Update Gameboard
		// Check player life status
		// Send command
		// Kill Connection

	}
	
	

	public SpectatorMain() {
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
					String gameString = new String(data).trim();
					
					if (gameString.equals("END_GAME")) {
						continue;
					}
					
					gameString += '\n';
					GameBoard b = new GameBoard(gameString.toCharArray());
					view.update(b);
					
					
				}
			}
		}).start();
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

