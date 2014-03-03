package serverLogic;


import java.util.concurrent.Semaphore;
import entities.*;
import Networking.Message;
import Networking.Network;
import java.util.Collections;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//TODO: construct logic Manager with playerlist 
public class NetworkManager {
	private Semaphore inboxLock;
	private Network net;
	private LogicManager logic;
	private boolean gameInProgress;
	private UserManager userManager;
	
	public NetworkManager() {
		gameInProgress = false;
		// create userManger
		userManager = new UserManager();
		// create logic manager with empty playerlist that we both share
		logic = new LogicManager(userManager.getCurrentPlayerList(),gameInProgress);
		// create network listening with messageinobx semaphore
		inboxLock = new Semaphore(0);
		net = new Network(Network.SEVER_PORT_NO, inboxLock);
		// start threads
		new Thread(net).start();
		new Thread(logic).start();

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

	private String readCommand(Message m){
		return new String( m.datagram.getData());
	}
	
	private void startCommand(String playerIP, int playerPort){
		// ???? logicManager.start()
	}
	private void endGameCommand(String playerIP, int playerPort){
		// remove player current playerlist
	}
	private void endSpectateCommand(String playerIP, int playerPort){
		// remove player from spectate list
	}
	private void joinCommand(String playerIP, int playerPort){
		if(gameInProgress){
			try{
				userManager.addPlayerToFuture(playerIP, playerPort);
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
		userManager.addSpectator(playerIp, playerPort);
		}catch(Exception E){System.out.println("added same player to specator twice");}
	}
}
