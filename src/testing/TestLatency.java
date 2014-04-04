package testing;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.junit.Test;

import Networking.Network;
import Networking.UserMessage;

public class TestLatency {
	private Semaphore inboxLock;
	private Network reciever;
	private Network sender;
	private ArrayList<String> strings = new ArrayList<String>();
	
	@Test
	public void latencyLocalSingle() {
		latencyLocalOne();
		
		int count = 10;
		long sumOfTimes = 0;
		for (int i = 0; i < count; i++) {
			sumOfTimes += latencyLocalOne();
		}
		
		System.out.println("Average latency (response time) for one message: " + sumOfTimes / count);
	}

	private long latencyLocalOne() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		inboxLock = new Semaphore(0);
		startListnerAndReciever();
		// create sender and reciever with
		long start = System.nanoTime();
		UserMessage m = new UserMessage("marco!", "127.0.0.1",
				Network.SERVER_PORT_NO, System.nanoTime());
		sender.sendMessage(m);
		try {
			inboxLock.acquire();
		} catch (Exception e) {
			System.out.println(e);
		}
		m = reciever.getMessage();
		UserMessage m2 = new UserMessage("polo!", "127.0.0.1",
				Network.CLIENT_PORT_NO, System.nanoTime());
		reciever.sendMessage(m2);
		try {
			inboxLock.acquire();
		} catch (Exception e) {
		}
		m = sender.getMessage();
		long time = System.nanoTime();
		System.out.println("response time for 1 message : "
				+ (time - start) + " nanoseconds");
		reciever.shutdown();
		sender.shutdown();
		
		return time - start;
	}

	private long latencyLocalMultiple(int count) {
		// wait for binded ports to clear
//		try {
//			Thread.sleep(2000);
//		} catch (Exception e) {
//		}
		inboxLock = new Semaphore(0);
		startListnerAndReciever();
		long start = System.nanoTime();
		UserMessage m;
		for (int i = 0; i < count; i++) {
			m = new UserMessage("marco!", "127.0.0.1", Network.SERVER_PORT_NO,
					System.nanoTime());
			sender.sendMessage(m);
		}
		for (int i = 0; i < count; i++) {
			try {
				inboxLock.acquire();
			} catch (Exception e) {
				System.out.println(e);
			}
			m = reciever.getMessage();
			UserMessage m2 = new UserMessage("polo!", "127.0.0.1",
					Network.CLIENT_PORT_NO, System.nanoTime());
			reciever.sendMessage(m2);
		}
		for (int i = 0; i < count; i++) {
			try {
				inboxLock.acquire();
			} catch (Exception e) {
			}
			m = sender.getMessage();
		}
		long time = System.nanoTime();
		System.out.println("roundtrip for 5 messages : "
				+ (time - start) + " nanoseconds");
		reciever.shutdown();
		sender.shutdown();
		
		return time - start;
	}

	public void startListnerAndReciever() {
		reciever = new Network(Network.SERVER_PORT_NO, inboxLock);
		sender = new Network(Network.CLIENT_PORT_NO, inboxLock);
		new Thread(reciever).start();
		new Thread(sender).start();
	}
}
