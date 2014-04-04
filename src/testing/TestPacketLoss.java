package testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Networking.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Networking.*;


public class TestPacketLoss {
	private Semaphore inboxLock;
	private Network reciever;
	private Network sender;
	int lossCount = 0;
	int messagesSent = 100;
	int messagesReceived = 0;
	double packetLoss = 0;
	ArrayList<String> strings = new ArrayList<String>();
	

	@Test
	public void test100() {
		org.apache.log4j.Logger.shutdown();
		packetLoss = 0;
		testMessage(100);
		testMessage(100);
		testMessage(100);
		testMessage(100);
		testMessage(100);
		testMessage(100);
		testMessage(100);
		testMessage(100);
		testMessage(100);
		testMessage(100);
		System.out.println("Average packet loss for 100 Messages Transmitted : " + (packetLoss/10.0) + "%");
	}
	@Test
	public void test500() {
		org.apache.log4j.Logger.shutdown();
		packetLoss = 0;
		testMessage(500);
		testMessage(500);
		testMessage(500);
		testMessage(500);
		testMessage(500);
		testMessage(500);
		testMessage(500);
		testMessage(500);
		testMessage(500);
		testMessage(500);
		System.out.println("Average packet loss for 500 Messages Transmitted : " + (packetLoss/10.0) + "%");
		
	}
	@Test
	public void test1000() {
		org.apache.log4j.Logger.shutdown();
		packetLoss = 0;
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		testMessage(1000);
		System.out.println("Average packet loss for 1000 Messages Transmitted : " + (packetLoss/10.0) + "%");
		/*
		for(String l : strings){
			System.out.println(l);}*/
	}
	
	@Test
	public void testLargeMessages() {
		org.apache.log4j.Logger.shutdown();
		packetLoss = 0;
		testMessage(2000);
		testMessage(5000);
		testMessage(10000);
		for(String l : strings){
			System.out.println(l);
		}

	}
	
	public void testMessage(int count){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		lossCount = 0;
		messagesSent = count;
		messagesReceived = 0;
		
		inboxLock = new Semaphore(0);
		startNetwork();
		// create sender and reciever with 
		long time = System.currentTimeMillis();
		
		for(int i = 0; i <messagesSent; i++){
			String message = "test";
			UserMessage m = new UserMessage(message, "127.0.0.1",Network.SERVER_PORT_NO, time);
			sender.sendMessage(m);
		}
		
		UserMessage in = new UserMessage(" ", null, 0, time);
		
		for(int i=0; i< messagesSent;i++){
			try{
				boolean rb =inboxLock.tryAcquire(1000, TimeUnit.MILLISECONDS);
				if(rb == false)
					break;
			}catch(Exception e){System.out.println(e);}
			in= reciever.getMessage();
			
		if(in.message != null)
			messagesReceived++;
		}
		packetLoss += (100 -(messagesReceived/1.0)/messagesSent * 100);
		System.out.println("Messages transmitted : " + count + ", Messages received : " + messagesReceived +" , PACKET LOSS(%) : " + (100 -(messagesReceived/1.0)/messagesSent * 100)  + "%");
		reciever.shutdown();
		sender.shutdown();
	}
	public void startNetwork(){
		 reciever = new Network(Network.SERVER_PORT_NO, inboxLock);
		 sender = new Network(Network.CLIENT_PORT_NO,inboxLock);
		new Thread(reciever).start();
		new Thread(sender).start();
	}
}
