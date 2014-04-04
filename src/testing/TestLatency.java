package testing;

import static org.junit.Assert.*;

import org.junit.Test;

import Networking.Network;

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
import java.util.concurrent.TimeUnit;

import Networking.*;


public class TestLatency {
	private Semaphore inboxLock;
	private Network reciever;
	private Network sender;
	@Test
	public void LatencyLocal0() {
		inboxLock = new Semaphore(0);
		startListnerAndReciever();
		// create sender and reciever with 
		String message = new String().valueOf(System.currentTimeMillis());
		UserMessage m = new UserMessage(message, "127.0.0.1",Network.SERVER_PORT_NO, System.currentTimeMillis());
		sender.sendMessage(m);
		try{
			inboxLock.acquire();
		}catch(Exception e){System.out.println(e);}
		m = reciever.getMessage();
		String message2 = new String().valueOf(System.currentTimeMillis());
		UserMessage m2 = new UserMessage(message2, "127.0.0.1",Network.CLIENT_PORT_NO, System.currentTimeMillis());
		reciever.sendMessage(m2);
		try{
			inboxLock.acquire();
		}catch(Exception e){}
		m = sender.getMessage();
		long time =Long.valueOf( m.message);
		System.out.println("response time for 1 message : "+ (System.currentTimeMillis()-time));
		reciever.shutdown();
		sender.shutdown();
	}
	
	@Test
	public void LatencyLocal1() {
		System.out.println("waiting for ports to clear...");
		try{
		Thread.sleep(2000);
		}catch(Exception e){}
		System.out.println("starting test 2");
		inboxLock = new Semaphore(0);
		startListnerAndReciever();
		long t = System.currentTimeMillis(); 
		UserMessage m;
		for(int i= 0; i< 5; i++){
			String message = new String().valueOf(t);
			 m = new UserMessage(message, "127.0.0.1",Network.SERVER_PORT_NO, System.currentTimeMillis());
			sender.sendMessage(m);
		}
		for(int i = 0; i< 5; i++){
			try{
				inboxLock.acquire();
			}catch(Exception e){System.out.println(e);}
			m = reciever.getMessage();
			String message2 = new String().valueOf(System.currentTimeMillis());
			UserMessage m2 = new UserMessage(message2, "127.0.0.1",Network.CLIENT_PORT_NO, System.currentTimeMillis());
			reciever.sendMessage(m2);
		}
		long time = 0;
		for(int i =0; i<5; i++){
			try{
				inboxLock.acquire();
			}catch(Exception e){}
			m = sender.getMessage();
			 time =Long.valueOf( m.message);
		}
		System.out.println("roundtrip for 5 messages : "+ (System.currentTimeMillis()-time));
		reciever.shutdown();
		sender.shutdown();
	}
	
	
	
	public void startListnerAndReciever(){
		 reciever = new Network(Network.SERVER_PORT_NO, inboxLock);
		 sender = new Network(Network.CLIENT_PORT_NO,inboxLock);
		new Thread(reciever).start();
		new Thread(sender).start();
	}
}
