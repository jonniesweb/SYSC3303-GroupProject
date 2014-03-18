package serverLogic;


import java.util.List;
import java.util.concurrent.Semaphore;

import Networking.UserMessage;
import Networking.Network;

import org.apache.log4j.Logger;



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
	private final static Logger LOG = Logger.getLogger(NetworkManager.class.getName());
	/**
	 * 
	 */
	public NetworkManager(LogicManager logic, UserManager m) {
		this.logic = logic;
		
		
		inboxLock = new Semaphore(0);
		net = new Network(Network.SERVER_PORT_NO, inboxLock);
		
		userManager = m;
		new Thread(this).start();
	}
	
	/**
	 * 
	 */
	public void run(){

		new Thread(net).start();
		running = true;
		UserMessage userMessage;
		
		while(running){
			
			userMessage = readInbox();
			//log.acceptMessage(new String(message.datagram.getData()));
<<<<<<< HEAD
			//Logger.acceptMessage("Read data from inbox - " + new String(message.datagram.getData()) + "- from " + message.ip);			
			// should join game before starting game
			if (readCommand(message).equals("START_GAME")){
				//System.out.println("got start game command");
=======
			Logger.acceptMessage("Read data from inbox - " + new String(userMessage.datagram.getData()) + "- from " + userMessage.ip);			
			// should join game before starting game
			if (readCommand(userMessage).equals("START_GAME")){
				System.out.println("got start game command");
>>>>>>> MessageRefactor
				if(!logic.getGameInProgress()){
					//System.out.println("game is not in progress");
					if(userManager.getCurrentPlayerList().size()> 0){
						//System.out.println("playerList is greater than 0");
						logic.setGameInProgress(true);
						
						// XXX: network manager should never create a LogicManager
						//logic = new LogicManager(userManager, log, this);
					}else{
						LOG.info("attempted to join before start");
					}
				}
				continue;
			}
			if (readCommand(userMessage).equals("JOIN_GAME")){
				joinCommand(userMessage.datagram.getAddress().toString(), userMessage.datagram.getPort());
				continue;
			}
			else if (readCommand(userMessage).equals("SPECTATE_GAME")){
				spectate(userMessage.datagram.getAddress().toString(), userMessage.datagram.getPort());
				continue;
			}
<<<<<<< HEAD
			else if (readCommand(message).equals("END_GAME") && logic.getGameInProgress()){
				//System.out.println("got end game.. game in progress is: "+ logic.getGameInProgress());
				endGameCommand(message.datagram.getAddress().toString(), message.datagram.getPort());
=======
			else if (readCommand(userMessage).equals("END_GAME") && logic.getGameInProgress()){
				System.out.println("got end game.. game in progress is: "+ logic.getGameInProgress());
				endGameCommand(userMessage.datagram.getAddress().toString(), userMessage.datagram.getPort());
>>>>>>> MessageRefactor
				continue;
			}
			else {
				if(logic.getGameInProgress())
					logic.execute(userMessage);
			}	
		}	
	}
	
	/**
	 * 
	 * @return
	 */
	private UserMessage readInbox() {
		try {
			inboxLock.acquire();
		} catch (InterruptedException e) {
			LOG.error("Thread Interrupted returning empty message");
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
			UserMessage m = new UserMessage(board,ip,port,System.nanoTime());
			net.sendMessage(m);
		}
	}
	
	/**
	 * 
	 */
	public void sendEndGameToAllClients(){
		LOG.info("END GAME SENT TO ALL CLIENT");
		String endGame = "END_GAME";
		List<User> users = userManager.getAllUsers();
		for (int i = 0; i < users.size(); i++) {
			String ip = users.get(i).getIp();
			int port = users.get(i).getPort();
			UserMessage m = new UserMessage(endGame, ip, port,System.nanoTime());
			net.sendMessage(m);
		}
		logic.setGameInProgress(false);
		
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	private String readCommand(UserMessage m) {
		String s = new String(m.datagram.getData()).trim();
		return s;
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
				//Logger.acceptMessage("Player from " + playerIP + "port : "+ playerPort + " END THE GAME");
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
				LOG.info("Player from " + playerIP + "port : "+ playerPort + " JOIN GAME");

			} catch (Exception E) {
				LOG.error("ERROR - ADDED SAME PLAYER TO FUTURE TWICE");
			}
		} else {
			try {
				userManager.addPlayerToCurrent(playerIP, playerPort);
			} catch (Exception E) {
				LOG.error("ERROR - ADDED SAME PLAYER TO CURRENT TWICE");
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
			LOG.info("Player from " + playerIP + "port : "+ playerPort + " SPECTATE GAME");
		} catch (Exception E) {
			LOG.error("ERROR - ADDED SAME PLAYER TO SPECTATOR TWICE");
		}
	}


	
}

