package serverLogic;


import java.util.List;
import java.util.concurrent.Semaphore;

import Networking.Message;
import Networking.Network;


//TODO: construct logic Manager with playerlist 
//TODO: add mainLoop
public class NetworkManager implements Runnable{
	private boolean gameInProgress;
	private Semaphore inboxLock;
	private Network net;
	private LogicManager logic;
	private UserManager userManager;
	
	public NetworkManager() {
		gameInProgress = false;
		
		inboxLock = new Semaphore(0);
		net = new Network(Network.SEVER_PORT_NO, inboxLock);
		
		userManager = new UserManager();

	}
	
	/**
	 * 
	 */
	public void run(){
		
		
		
		new Thread(net).start();
		
		Message message;
		
		while(true){
			
			message = readInbox();
			
			if (message.getMessage().equals("START_GAME")){
				gameInProgress = true;
				logic = new LogicManager(userManager, this);
			}
			
			if (message.getMessage().equals("JOIN_GAME")){
				joinCommand(message.getIP(), message.getPort());
			}
			else if (message.getMessage().equals("SPECTATE_GAME")){
				specateCommand(message.getIP(), message.getPort());
			}
			else if (message.getMessage().equals("END_GAME") && gameInProgress){
				endGameCommand(message.getIP(), message.getPort());
			}
			else {
				if(gameInProgress)
					logic.execute(message.getMessage(), message.getIP());
			}	
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	private Message readInbox() {
		try {
			inboxLock.acquire();
		} catch (InterruptedException e) {
			System.out.println("Thread Interrupted returning empty message");
			return null;
		}
		return net.getMessage();
	}
	
	/**
	 * @deprecated
	 */
	private void sendBoardToAllClients() {
		String board = "NULL";// = logic.getBoard();
		List<User> users = userManager.getAllUsers();
		for (int i = 0; i < users.size(); i++) {
			String ip = users.get(i).getIp();
			int port = users.get(i).getPort();
			Message m = new Message(board, ip, port);
			net.sendMessage(m);
		}
	}

	private String readCommand(Message m) {
		return new String(m.datagram.getData());
	}
	
	// TODO tell logicManager to setGameBoard when we get start command
	private void startCommand(String playerIP, int playerPort) {
		// you must join game before starting
		if (userManager.getAllUsers().size() == 0) {
			System.out.println("User attempted to start game before join");
			return;
		}
		logic.start();

	}

	private void endGameCommand(String playerIP, int playerPort) {
		// remove player current playerlist
	}

	private void endSpectateCommand(String playerIP, int playerPort) {
		// remove player from spectate list
	}

	private void joinCommand(String playerIP, int playerPort) {
		if (gameInProgress) {
			try {

				userManager.addPlayerToFuture(playerIP, playerPort);

			} catch (Exception E) {
				System.out.println("Added same player to future twice");
			}
		} else {
			try {
				userManager.addPlayerToCurrent(playerIP, playerPort);
			} catch (Exception E) {
				System.out.println("Added same player to current twice");
			}
		}
	}
	
	private void specateCommand(String playerIp, int playerPort) {
		try {

			userManager.addSpectator(playerIp, playerPort);
		} catch (Exception E) {
			System.out.println("added same player to specator twice");
		}
	}


	
}

