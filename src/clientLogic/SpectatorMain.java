package clientLogic;

import gameLogic.GameBoard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.Semaphore;

import Networking.Network;
import Networking.UserMessage;

public class SpectatorMain {
	
	protected Network network;
	private Semaphore inboxLock = new Semaphore(0);
	private boolean running = false;
	protected ClientGUI view;
	

	public static void main(String[] args) {

		// create dialog
		final ConnectDialog dialog = new ConnectDialog();
		
		ActionListener join = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get client port, server port, and hostname from dialog
				Random r = new Random();
				int cPort = 8000 + r.nextInt(100);
				if (dialog.getClientPort().length() > 0) {
					cPort = Integer.parseInt(dialog.getClientPort());
				}
				int sPort = Network.SERVER_PORT_NO;
				if (dialog.getServerPort().length() > 0) {
					sPort = Integer.parseInt(dialog.getServerPort());
				}
				String hostname = dialog.getServerIP();
				
				// start a new spectator window
				dialog.dispose();
				new SpectatorMain(cPort, hostname, sPort);
				
			}
		};
		
		ActionListener exit = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		};
		
		dialog.addConnectActionListener(join);
		dialog.addExitActionListener(exit);
		
		// show dialog
		dialog.setVisible(true);

		// Loop while running
		// Establish Connection
		// Loop
		// Update Gameboard
		// Check player life status
		// Send command
		// Kill Connection

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
		
		startListening();
		
		network.sendMessage(new UserMessage(connectCommand, ip, port, time));
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

