package Networking;




import java.io.*; 
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Network extends Thread{
	private DatagramSocket socket;
	private ExecutorService pool;
	private LinkedList<InMessage> inbox;
	int port = 8888;
	
	// constructor 
	public Network(){
		pool = Executors.newCachedThreadPool();
		inbox = (LinkedList<InMessage>)Collections.synchronizedList(new LinkedList<InMessage>());
	}
	// check if we have something in inbox
	public boolean hasMessage(){
		return (inbox.size()> 0);
	}
	// get ip of first message in inbox
	public byte[] readInboxAddress(){
		if(hasMessage()){
			byte[] ip = inbox.get(0).ip.getAddress();
			return ip;
		}else{
			System.out.println("No message in inbox returning null");
			return null;
		}
	}
	// removes message from inbox and returns message value
	public String readInboxMessage(){
		if(hasMessage()){
			return(new String(inbox.remove(0).message.getData()));
		}else{
			System.out.println("No message in inbox returning null");
			return null;
		}
	}
	//get port of first message in inbox
	public int readInboxPort(){
		if(hasMessage()){
			int p = inbox.get(0).packetPort;
			return p;
		}else{
			System.out.println("No message in inbox returning -1");
			return -1;
		}
	}
	// sendMessage client version
	public void sendMessage(String m){
			try{
				InetAddress IPAddress = InetAddress.getByName("localhost"); 
				DatagramPacket sendPacket = new DatagramPacket(m.getBytes(),
											m.getBytes().length,
											IPAddress,port);
				socket.send(sendPacket);
			}catch(Exception e){System.out.println("Sending error: "+ e);}
	}
	
	public void sendMessage(String m, byte[] ip, int packetPort){
		pool.submit((Runnable) new OutMessage(m,ip,packetPort));
	}
	// listen then start acceptloop
	private void startListening(){
		try{
			socket = new DatagramSocket(port);
			acceptLoop();
		}catch(Exception e){System.out.println("Socket error "+ e);}
	}
	// threaded message accepting
	private void acceptLoop() throws IOException{
		while(true){
			byte[] inBuffer = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(inBuffer, inBuffer.length);
			socket.receive(receivePacket);
			pool.submit((Runnable) new InMessage(receivePacket));
		}
		
	}
	
	// start the listen loop in a new thread
	public void run(){
		startListening();
	}
	// inner class for recieve messages in new threads
	public class InMessage implements Runnable{
		public DatagramPacket message;
		public InetAddress ip;
		public int packetPort;
		public InMessage(DatagramPacket p){
			message = p;
			ip = p.getAddress();
			packetPort = p.getPort();
		}
		public void run(){
			inbox.add(this);
		}
	}
	
	public class OutMessage implements Runnable{
		private int packetPort;
		private InetAddress ip;
		public DatagramPacket message;
		
		public OutMessage(String m, byte[] ipAddr, int p){
			this.packetPort = p;
			try{ip = InetAddress.getByAddress(ipAddr);}
			catch(Exception e){System.out.println(e);}
			message  = new DatagramPacket(m.getBytes(),m.getBytes().length,ip,packetPort);
		}
		public void run(){
			try{
				socket.send(message);
			}catch(Exception e){System.out.println(e);}
		}
	}

// end of file
}