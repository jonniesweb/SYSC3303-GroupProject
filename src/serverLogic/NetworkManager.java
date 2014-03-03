package serverLogic;


import java.util.List;
import java.util.concurrent.Semaphore;

import Networking.Message;
import Networking.Network;

import testing.Logger;

import java.util.List;



//TODO: construct logic Manager with playerlist 
//TODO: add mainLoop
public class NetworkManager implements Runnable{
	private boolean gameInProgress;
	private Semaphore inboxLock;
	private Network net;
	private LogicManager logic;
	private UserManager userManager;
	private Logger log;
	private boolean running;
	
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
		running = true;
		Message message;
		
		log = new Logger();
		
		while(running){
			
			message = readInbox();
			log.acceptMessage(new String(message.datagram.getData()));
			
			if (readCommand(message).equals("START_GAME")){
				gameInProgress = true;
				logic = new LogicManager(userManager, log, this);
			}
			
			if (readCommand(message).equals("JOIN_GAME")){
				joinCommand(message.datagram.getAddress().toString(), message.datagram.getPort());
			}
			else if (readCommand(message).equals("SPECTATE_GAME")){
				specateCommand(message.datagram.getAddress().toString(), message.datagram.getPort());
			}
			else if (readCommand(message).equals("END_GAME") && !gameInProgress){
				endGameCommand(message.datagram.getAddress().toString(), message.datagram.getPort());
			}
			else {
				if(gameInProgress)
					logic.execute(message);
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
	public void sendBoardToAllClients() {
		String board = "NULL";// = logic.getBoard();
		List<User> users = userManager.getAllUsers();
		for(int i=0; i< users.size(); i++){
			String ip = users.get(i).getIp();
			int port = users.get(i).getPort();
			Message m = new Message(board,ip,port);
			net.sendMessage(m);
		}
	}
	
	public void sendEndGameToAllClients(){
		String board = "END_GAME";
		List<User> users = userManager.getAllUsers();
		for (int i = 0; i < users.size(); i++) {
			String ip = users.get(i).getIp();
			int port = users.get(i).getPort();
			Message m = new Message(board, ip, port);
			net.sendMessage(m);
		}
		gameInProgress = false;
		log.writeLog();
		
	}

	private String readCommand(Message m) {
		return new String(m.datagram.getData());
	}
	
	private void endGameCommand(String playerIP, int playerPort){
		// remove player current playerlist
		for(User u: userManager.getAllUsers()){
			if(u.getIp().equals(playerIP)){
				//remove Users
			}
		}
	}
	
	/**
	 * 
	 * @param playerIP
	 * @param playerPort
	 */
	private void endSpectateCommand(String playerIP, int playerPort){

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

