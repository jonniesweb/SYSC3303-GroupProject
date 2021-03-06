package Networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

// TODO: add timestamp to all sent commands, then add to inbox SortedList
public class Network extends Thread {
	private DatagramSocket socket;
	private ExecutorService pool;
	private List<UserMessage> inbox;
	public static final int SERVER_PORT_NO = 8888;
	public static final int CLIENT_PORT_NO = 8871;
	int port;
	private Semaphore inboxLock;
	private static final Logger LOG = Logger.getLogger(Network.class.getName());
	private int count = 0;
	
	private boolean isReady = false;
	
	private String networkName = "default";

	// constructor
	public Network(int p, Semaphore lock) {
		port = p;
		pool = Executors.newCachedThreadPool();
		inbox = Collections.synchronizedList(new LinkedList<UserMessage>());
		inboxLock = lock;
		// just incase lock was initialized with number other than 0
		inboxLock.drainPermits();
		//Logger log = Logger.getLogger("Global");
	}
	
	public Network(int p, Semaphore lock, String n){
		this(p, lock);
		networkName = n;
	}
	
	public void shutdown(){
		LOG.info("NETWORK SHUTDOWN : " + networkName);
		socket.close();
		pool.shutdown();
	}

	/**
	 * check if we have something in inbox
	 * @return
	 */
	public boolean hasMessage() {
		return (inbox.size() > 0);
	}
	
	public UserMessage getMessage(){
		if(hasMessage()){
			LOG.info("returning message "+ port);
			return inbox.remove(0);
		}
		else{
			LOG.error("ERROR EMPTY INBOX RETURNING NULL sever is: "+ port);
			return null;
		}
	}
	/**
	 * Queue the specified usermessage to send. checks if the Network is ready
	 * to send messages, if it is not, it waits until woken up by
	 * startListening();
	 * @param m
	 */
	public void sendMessage(final UserMessage m) {
		Runnable r1 = new Runnable(){
			public void run(){
				
				// wait for network to be ready, if its not already
				while (!isReady) {
					try {
						socket.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try{
					socket.send(m.datagram);
					
				}catch(Exception e){
					LOG.error(e);
				}
			}
		};
		pool.submit(r1);
	}
	/**
	 * Starts listening by creating a DatagramSocket, notifies all threads
	 * that are waiting on the socket to send messages and starts accepting
	 * messages.
	 */
	private void startListening() {
		try {
			
			// create socket
			socket = new DatagramSocket(port);
			LOG.info("listning on port"+ port);
			
			// notify all waiting on the socket to let them know its able to send
			isReady = true;
			synchronized (socket) {
				socket.notifyAll();
			}
			
			// start accepting messages
			acceptLoop();
		} catch (Exception e) {
			LOG.error("SOCKET ERROR ("+port+") ["+networkName+"] " + e);
		}
	}

	/**
	 * threaded message accepting
	 * @throws IOException
	 */
	private void acceptLoop() throws IOException {
		//LOG.info("LISTENING ON PORT : " + port);
		
		while (!Thread.currentThread().isInterrupted()) {
			byte[] inBuffer = new byte[1024];
			
			final DatagramPacket receivePacket = new DatagramPacket(inBuffer,
					inBuffer.length);
			Runnable r1 = new Runnable(){
				public void run(){
					UserMessage m1 = new UserMessage(receivePacket);
					String command = new String(receivePacket.getData());
					//command.trim() will remove all null after the command for better log
					LOG.info("GOT MESSAGE from Inbox: "+ command.trim() +" port is:"+port);
					inbox.add(m1);
					inboxLock.release(1);
				}
			};
			socket.receive(receivePacket);
			pool.submit(r1);
		}
		System.out.println("It got here yay");
		pool.shutdown();
		socket.close();
		System.out.println(networkName + " network has shutdown");
	}
	public int getSemCount(){
		return inboxLock.availablePermits();
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