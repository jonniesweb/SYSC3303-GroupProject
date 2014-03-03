package serverLogic;


import java.util.concurrent.Semaphore;

import Networking.Message;
import Networking.Network;

import java.util.List;


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
			
			if (readCommand(message).equals("START_GAME")){
				gameInProgress = true;
				logic = new LogicManager(userManager, this);
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
	

	public void sendBoardToAllClients(){
		String board = logic.getBoard();
		List<User> users = userManager.getAllUsers();
		for(int i=0; i< users.size(); i++){
			String ip = users.get(i).ip;
			int port = users.get(i).port;
			Message m = new Message(board,ip,port);
			net.sendMessage(m);
		}
	}
	
	public void sendEndGameToAllClients(){
		String board = "END_GAME";
		List<User> users = userManager.getAllUsers();
		for(int i=0; i< users.size(); i++){
			String ip = users.get(i).ip;
			int port = users.get(i).port;
			Message m = new Message(board,ip,port);
			net.sendMessage(m);
		}
		gameInProgress = false;
	}
	
	private String readCommand(Message m){
		return new String( m.datagram.getData());
	}
	
	//TODO tell logicManager to setGameBoard when we get start command
	/*private void startCommand(String playerIP, int playerPort){
		// you must join game before starting
		if(userManager.getAllUsers().size() == 0){
			System.out.println("User attempted to start game before join");
			return;
		}
		 logic.start();

	}*/
	
	private void endGameCommand(String playerIP, int playerPort){
		// remove player current playerlist
		for(User u: userManager.getAllUsers()){
			if(u.ip.equals(playerIP)){
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
	private void joinCommand(String playerIP, int playerPort){
		if(gameInProgress){
			try{

				userManager.addPlayerToFuture(playerIP,playerIP, playerPort);


			}
			catch(Exception E){System.out.println("Added same player to future twice");}
		}else{
			try{
				userManager.addPlayerToCurrent(playerIP, playerPort);
			}
			catch(Exception E){System.out.println("Added same player to current twice");}
		}
	}
	
	private void specateCommand(String playerIp, int playerPort){
		try{

		userManager.addSpectator(playerIp,playerIp, playerPort);
		}catch(Exception E){System.out.println("added same player to specator twice");}
	}



	
}

