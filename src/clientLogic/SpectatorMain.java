package clientLogic;

import gameLogic.GameBoard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import Networking.Network;
import Networking.UserMessage;

public class SpectatorMain {

	protected static Logger LOG = Logger.getLogger(SpectatorMain.class.getName());
	protected Network network;
	protected Semaphore inboxLock = new Semaphore(0);
	private boolean running = false;
	protected ConnectDialog dialog = new ConnectDialog();
	protected ClientGUI view = new ClientGUI();
	
	protected int clientPort;
	protected int serverPort;
	protected String serverHostname;

	public static void main(String[] args) {
		
		new SpectatorMain();

		// Loop while running
		// Establish Connection
		// Loop
		// Update Gameboard
		// Check player life status
		// Send command
		// Kill Connection

	}

	public SpectatorMain() {

		// create dialog

		ActionListener join = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get client port, server port, and hostname from dialog
				Random r = new Random();
				clientPort = 8000 + r.nextInt(100);
				if (dialog.getClientPort().length() > 0) {
					clientPort = Integer.parseInt(dialog.getClientPort());
				}
				LOG.info("Set client port to: " + clientPort);
				
				serverPort = Network.SERVER_PORT_NO;
				if (dialog.getServerPort().length() > 0) {
					serverPort = Integer.parseInt(dialog.getServerPort());
				}
				LOG.info("Set server port to: " + serverPort);

				serverHostname = dialog.getServerIP();
				LOG.info("Set server hostname to: " + serverHostname);

				// start a new spectator window
				dialog.dispose();
				startServer();
			}
		};

		ActionListener exit = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				LOG.info("Exiting program");
				System.exit(0);
			}
		};

		dialog.addConnectActionListener(join);
		dialog.addExitActionListener(exit);

		// show dialog
		dialog.setVisible(true);
		
		// send an END_GAME message to server when user exits client
		view.guiFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				sendMessage("END_GAME");
				LOG.info("Exiting program");
				System.exit(0);
			}
		});

		
	}

	protected void startServer() {
		// setup network
		network = new Network(clientPort, inboxLock);
		new Thread(network).start();
		LOG.info("Started Network");

		view.guiFrame.setVisible(true);

		// connect to server
		String connectCommand = "SPECTATE_GAME";

		startListening();
		LOG.info("Started listening");

		sendMessage(connectCommand);
	}

	/**
	 * listen for incoming data
	 */
	protected void startListening() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				running = true;
				UserMessage userMessage;
				while (running) {
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

	/**
	 * Send a string to the server
	 * @param data
	 */
	protected void sendMessage(String data) {
		network.sendMessage(new UserMessage(data, serverHostname, serverPort, System.nanoTime()));
		LOG.info("SENT COMMAND : " + data);
	}
	
	
}
