package clientLogic;

import gameLogic.GameBoard;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

import Networking.UserMessage;
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
		this(Network.CLIENT_PORT_NO, "127.0.0.1", Network.SERVER_PORT_NO);
	
		
	}



	public SpectatorMain(int clientPort, String serverHostname, int serverPort) {
		// setup network
		network = new Network(clientPort, inboxLock);
		new Thread(network).start();
		view = new ClientGUI();
		
		// connect to server
		String connectCommand = "SPECTATE_GAME";
		int port = serverPort;
		long time = System.nanoTime();
		String ip = serverHostname;
		try {
			ip = InetAddress.getLocalHost().getHostAddress(); // XXX: remove since it's a hack
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		startListening();
		
//		network.sendMessage(new UserMessage(connectCommand, ip, port, time));
	}
	
	/**
	 * listen for incoming data
	 */
	private void startListening() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				running = true;
				UserMessage userMessage;
				while(running) {
					userMessage = readInbox();
					byte[] data = userMessage.datagram.getData();
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



	private UserMessage readInbox() {
		try {
			inboxLock.acquire();
		} catch (InterruptedException e) {
			System.out.println("Thread Interrupted returning empty message");
			return null;
		}
		return network.getMessage();
	}
}

