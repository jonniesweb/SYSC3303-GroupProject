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


public class latencyTest {
	private Semaphore inboxLock;
	private Network reciever;
	private Network sender;
	@Test
	public void Latency_level_0() {
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
		long dif = System.currentTimeMillis()-Long.valueOf(m.message);
		System.out.println("Latency in milliseconds for sending and recieving 1 message"+dif);
		reciever.shutdown();
		sender.shutdown();
	}
	
	@Test
	public void Latency_level_1() {
		System.out.println("waiting for ports to clear...");
		try{
		Thread.sleep(2000);
		}catch(Exception e){}
		System.out.println("starting test 2");
		inboxLock = new Semaphore(0);
		startListnerAndReciever();
		// create sender and reciever with 
		long time = System.currentTimeMillis();
		for(int i = 0; i <10; i++){
			String message = new String().valueOf(time);
			UserMessage m = new UserMessage(message, "127.0.0.1",Network.SERVER_PORT_NO, time);
			sender.sendMessage(m);
		}
		UserMessage in = new UserMessage(" ", null, 0, time);
		for(int i=0; i< 10;i++){
			try{
				boolean rb =inboxLock.tryAcquire(1000, TimeUnit.MILLISECONDS);
				if(rb == false)
					break;
			}catch(Exception e){System.out.println(e);}
			in= reciever.getMessage();
		}
		if(in.message == null)
			fail("there was packetLoss");
		long dif = System.currentTimeMillis()-Long.valueOf(in.message);
		System.out.println("Latency in milliseconds for sending and recieving 10 message"+dif);
		System.out.println("Average latency perMessage: "+dif/10);
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
