package serverLogic;
import java.util.concurrent.*;

import Networking.*;

//TODO: construct logic Manager with playerlist 
public class NetworkManager {
	private Semaphore inboxLock;
	private Network   net;
	private LogicManager logic;
	private String command;
	
	public NetworkManager(){
		//logic = new LogicManger();
		inboxLock =  new Semaphore(0);
		net = new Network(Network.SEVER_PORT_NO,inboxLock);
		new Thread(net).start();
		new Thread(logic).start();
		
	}
	private Message readInbox(){
		try{
			inboxLock.acquire();
		}catch(InterruptedException e){
			System.out.println("Thread Interrupted returning empty message");
			return null;
		}
		return net.getMessage();
	}
	
	
}
