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

// TODO: add timestamp to all sent commands, then add to inbox SortedList
public class Network extends Thread {
	private DatagramSocket socket;
	private ExecutorService pool;
	private List<Message> inbox;
	public static final int SEVER_PORT_NO = 8888;
	public static final int CLIENT_PORT_NO = 8871;
	int port;
	private Semaphore inboxLock;

	// constructor
	public Network(int p, Semaphore lock) {
		port = p;
		pool = Executors.newCachedThreadPool();
		inbox = Collections.synchronizedList(new LinkedList<Message>());
		inboxLock = lock;
		// just incase lock was initialized with number other than 0
		inboxLock.drainPermits();
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
			System.out.println("Error empty inbox returning null");
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
				}catch(Exception e){System.out.println("Sending error: "+ e);}
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
			System.out.println("Socket error " + e);
		}
	}

	/**
	 * threaded message accepting
	 * @throws IOException
	 */
	private void acceptLoop() throws IOException {
		System.out.println("listening on port: " + port);
		while (true) {
			byte[] inBuffer = new byte[1024];
			
			final DatagramPacket receivePacket = new DatagramPacket(inBuffer,
					inBuffer.length);
			Runnable r1 = new Runnable(){
				public void run(){
					Message m1 = new Message(receivePacket);
					String command = new String(receivePacket.getData());
					System.out.println("got command: "+ command);
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