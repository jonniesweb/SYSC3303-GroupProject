package serverLogic;


import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import Networking.UserMessage;
import Networking.Network;

import org.apache.log4j.Logger;

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
	
	/*
	 * Create a double buffer out of an ArrayBlockingQueue. This is possible
	 * because whenever the sendBoardToAllClients method is called, the method
	 * checks if a gameboard already exists in the buffer of size 1. If it
	 * exists it clears the buffer and places the new gameboard into it. If its
	 * empty, just add it.
	 * 
	 * The setupSendBoardToAllClients() method creates a thread that gets and
	 * blocks on taking a gameboard update from the gameboardUpdateQueue
	 * whenever it is full or empty, respectively.
	 */
	private ArrayBlockingQueue<String> gameboardUpdateQueue = new ArrayBlockingQueue<String>(1);
	
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
		
		setupSendBoardToAllClients();
	}
	
	public void shutdown(){
		Network.shutdown();
		this.running = false;
	}
	
	/**
	 * Spawns a thread to take a gameboard from the gameboard double buffer and
	 * send it out to all users. Runs forever.
	 */
	private void setupSendBoardToAllClients() {
		new Thread(new Runnable() {

			
			@Override
			public void run() {

				while (true) {

					String board;
					// take the board from the double buffer
					try {
						// block until a new gameboard is avaliable
						board = gameboardUpdateQueue.take();
						List<User> users = userManager.getAllUsers();

						// send out the gameboard to all users
						for (int i = 0; i < users.size(); i++) {
							String ip = users.get(i).getIp();
							int port = users.get(i).getPort();
							UserMessage m = new UserMessage(board, ip, port,
									System.nanoTime());
							net.sendMessage(m);
						}
					} catch (InterruptedException e) {
						LOG.error("Failed to take from gameboard update buffer");
					}
				}

			}
		}).start();
	}

	/**
	 * Reads messages from the network inbox queue. Sends valid messages to the
	 * Logic Manager and manages adding and removing users to the User Manager. 
	 */
	public void run(){

		new Thread(net).start();
		running = true;
		UserMessage userMessage;
		
		while(running){
			
			userMessage = readInbox();
			//log.acceptMessage(new String(message.datagram.getData()));
			// Logger.acceptMessage("Read data from inbox - " + new String(userMessage.datagram.getData()) + "- from " + userMessage.ip);			
			// should join game before starting game
			if (readCommand(userMessage).equals("START_GAME")){
				//System.out.println("got start game command");
				if(!logic.getGameInProgress()){
					//System.out.println("game is not in progress");
					if(userManager.getCurrentPlayerList().size()> 0){
						LOG.info("SETTING GAME PROGRESS TO TRUE");
						logic.setGameInProgress(true);
						
					}else{
						LOG.error("attempted to join before start");
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
			else if (readCommand(userMessage).equals("END_GAME") && logic.getGameInProgress()){
				System.out.println("got end game.. game in progress is: "+ logic.getGameInProgress());
				endGameCommand(userMessage.datagram.getAddress().toString(), userMessage.datagram.getPort());
				continue;
			}
			else {
				if(logic.getGameInProgress())
					logic.execute(userMessage);
			}	
		}	
	}
	
	/**
	 * Get a UserMessage from the network inbox queue, or block until it
	 * receives one
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
		
		if (gameboardUpdateQueue.offer(board)) {
			return;
		} else {
			gameboardUpdateQueue.clear();
			try {
				gameboardUpdateQueue.add(board);
			} catch (IllegalStateException e) {
				LOG.error("Failed to add the GameBoard to the update buffer");
			}
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

