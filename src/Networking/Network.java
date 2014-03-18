package Networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import serverLogic.LogicManager;

// TODO: add timestamp to all sent commands, then add to inbox SortedList
public class Network extends Thread {
	private DatagramSocket socket;
	private ExecutorService pool;
	private List<Message> inbox;
	public static final int SERVER_PORT_NO = 8888;
	public static final int CLIENT_PORT_NO = 8871;
	int port;
	private Semaphore inboxLock;
	//private static final Logger LOG = Logger.getLogger("TEST");

	// constructor
	public Network(int p, Semaphore lock) {
		port = p;
		pool = Executors.newCachedThreadPool();
		inbox = Collections.synchronizedList(new LinkedList<Message>());
		inboxLock = lock;
		// just incase lock was initialized with number other than 0
		inboxLock.drainPermits();
		//Logger log = Logger.getLogger("Global");
	}

	/**
	 * check if we have something in inbox
	 * @return
	 */
	public boolean hasMessage() {
		return (inbox.size() > 0);
	}
	
	public Message getMessage(){
		if(hasMessage())
			return inbox.remove(0);
		else{
			//LOG.error("ERROR EMPTY INBOX RETURNING NULL");
			return null;
		}
	}
	/**
	 * sendMessage 
	 * @param m
	 */
	public void sendMessage(final Message m) {
		Runnable r1 = new Runnable(){
			public void run(){
				try{
					socket.send(m.datagram);
				}catch(Exception e){
					}//LOG.error(e);
			}
		};
		pool.submit(r1);
	}
	/**
	 * listen then start acceptloop
	 */
	private void startListening() {
		try {
			socket = new DatagramSocket(port);
			acceptLoop();
		} catch (Exception e) {
			//LOG.error("SOCKET ERROR" + e);
		}
	}

	/**
	 * threaded message accepting
	 * @throws IOException
	 */
	private void acceptLoop() throws IOException {
		//LOG.info("LISTENING ON PORT : " + port);
		while (true) {
			byte[] inBuffer = new byte[1024];
			
			final DatagramPacket receivePacket = new DatagramPacket(inBuffer,
					inBuffer.length);
			Runnable r1 = new Runnable(){
				public void run(){
					Message m1 = new Message(receivePacket);
					String command = new String(receivePacket.getData());
					//LOG.info("GOT COMMAND: "+ command);
					inbox.add(m1);
					inboxLock.release(1);
				}
			};
			socket.receive(receivePacket);
			pool.submit(r1);
		}

	}

	/**
	 * start the listen loop in a new thread
	 */
	public void run() {
		startListening();
	}



	/**
	 * Testing function
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	// end of file
}