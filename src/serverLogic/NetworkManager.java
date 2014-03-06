package serverLogic;


import java.util.List;
import java.util.concurrent.Semaphore;

import Networking.Message;
import Networking.Network;

import testing.Logger;



//TODO: construct logic Manager with playerlist 
//TODO: add mainLoop
//TODO: when a new user joins, add user to Usermanager.addNewPlayer() then call LogicManager.notifyNewPlayer() or an equivalent method.
/**
 * 
 */
public class NetworkManager implements Runnable{
	//private boolean gameInProgress;
	private Semaphore inboxLock;
	private Network net;
	private LogicManager logic;
	private UserManager userManager;
	private boolean running;
	
	/**
	 * 
	 */
	public NetworkManager(LogicManager logic) {
		this.logic = logic;
		
		logic.setGameInProgress(false);
		
		inboxLock = new Semaphore(0);
		net = new Network(Network.SEVER_PORT_NO, inboxLock);
		
		userManager = new UserManager();
		new Thread(this).start();
	}
	
	/**
	 * 
	 */
	public void run(){

		new Thread(net).start();
		running = true;
		Message message;
		
		while(running){
			
			message = readInbox();
			//log.acceptMessage(new String(message.datagram.getData()));
			Logger.acceptMessage("Read data from inbox - " + new String(message.datagram.getData()) + "- from " + message.ip);			
			// should join game before starting game
			if (readCommand(message).equals("START_GAME")){

				if(!logic.getGameInProgress()){
					if(userManager.getCurrentPlayerList().size()> 0){
						logic.setGameInProgress(true);
						
						// XXX: network manager should never create a LogicManager
						//logic = new LogicManager(userManager, log, this);
					}else{
						System.out.println("attempted to join before start");
					}
				}
			}
			if (readCommand(message).equals("JOIN_GAME")){
				joinCommand(message.datagram.getAddress().toString(), message.datagram.getPort());
			}
			else if (readCommand(message).equals("SPECTATE_GAME")){
				spectate(message.datagram.getAddress().toString(), message.datagram.getPort());
			}
			else if (readCommand(message).equals("END_GAME") && !logic.getGameInProgress()){
				endGameCommand(message.datagram.getAddress().toString(), message.datagram.getPort());
			}
			else {
				if(logic.getGameInProgress())
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
	 * 
	 */
	public void sendBoardToAllClients(String board) {
		List<User> users = userManager.getAllUsers();
		for(int i=0; i< users.size(); i++){
			String ip = users.get(i).getIp();
			int port = users.get(i).getPort();
			Message m = new Message(board,ip,port);
			net.sendMessage(m);
		}
	}
	
	/**
	 * 
	 */
	public void sendEndGameToAllClients(){
		String endGame = "END_GAME";
		List<User> users = userManager.getAllUsers();
		for (int i = 0; i < users.size(); i++) {
			String ip = users.get(i).getIp();
			int port = users.get(i).getPort();
			Message m = new Message(endGame, ip, port);
			net.sendMessage(m);
		}
		logic.setGameInProgress(false);
		
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	private String readCommand(Message m) {
		return new String(m.datagram.getData());
	}
	
	/**
	 * 
	 * @param playerIP
	 * @param playerPort
	 */
	private void endGameCommand(String playerIP, int playerPort){
		// remove player current playerlist
		for(User u: userManager.getAllUsers()){
			if(u.getIp().equals(playerIP)){
				try{
				userManager.moveCurrentToFuture(u.getUUID());
				Logger.acceptMessage("Player from " + playerIP + "port : "+ playerPort + " END THE GAME");
				}catch(Exception e){
					e.printStackTrace();
					}
			}
		}
	}

	/**
	 * 
	 * @param playerIP
	 * @param playerPort
	 */
	private void joinCommand(String playerIP, int playerPort) {
		if (logic.getGameInProgress()) {
			try {

				userManager.addPlayerToFuture(playerIP, playerPort);
				Logger.acceptMessage("Player from " + playerIP + "port : "+ playerPort + " JOIN GAME");

			} catch (Exception E) {
				System.out.println("Added same player to future twice");
			}
		} else {
			try {
				userManager.addPlayerToCurrent(playerIP, playerPort);
			} catch (Exception E) {
				System.out.println("Added same player to current twice");
			}
			logic.incrementPlayerCount();
		}
	}
	
	/**
	 * 
	 * @param playerIP
	 * @param playerPort
	 */
	private void spectate(String playerIP, int playerPort) {
		try {

			userManager.addSpectator(playerIP, playerPort);
			Logger.acceptMessage("Player from " + playerIP + "port : "+ playerPort + " SPECTATE GAME");
		} catch (Exception E) {
			System.out.println("added same player to specator twice");
		}
	}


	
}

