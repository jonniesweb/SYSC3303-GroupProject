package testing;

import java.util.concurrent.Semaphore;

import org.junit.Test;

import Networking.Network;
import Networking.UserMessage;

public class TestLatency {
	private Semaphore inboxLock;
	private Network reciever;
	private Network sender;

	@Test
	public void LatencyLocal0() {
		inboxLock = new Semaphore(0);
		startListnerAndReciever();
		// create sender and reciever with
		String message = String.valueOf(System.nanoTime());
		UserMessage m = new UserMessage(message, "127.0.0.1",
				Network.SERVER_PORT_NO, System.nanoTime());
		sender.sendMessage(m);
		try {
			inboxLock.acquire();
		} catch (Exception e) {
			System.out.println(e);
		}
		m = reciever.getMessage();
		String message2 = String.valueOf(System.nanoTime());
		UserMessage m2 = new UserMessage(message2, "127.0.0.1",
				Network.CLIENT_PORT_NO, System.nanoTime());
		reciever.sendMessage(m2);
		try {
			inboxLock.acquire();
		} catch (Exception e) {
		}
		m = sender.getMessage();
		long time = Long.valueOf(m.message);
		System.out.println("response time for 1 message : "
				+ (System.nanoTime() - time) + " nanoseconds");
		reciever.shutdown();
		sender.shutdown();
	}

	@Test
	public void LatencyLocal1() {
		System.out.println("waiting for ports to clear...");
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		System.out.println("starting test 2");
		inboxLock = new Semaphore(0);
		startListnerAndReciever();
		long t = System.nanoTime();
		UserMessage m;
		for (int i = 0; i < 5; i++) {
			String message = String.valueOf(t);
			m = new UserMessage(message, "127.0.0.1", Network.SERVER_PORT_NO,
					System.nanoTime());
			sender.sendMessage(m);
		}
		for (int i = 0; i < 5; i++) {
			try {
				inboxLock.acquire();
			} catch (Exception e) {
				System.out.println(e);
			}
			m = reciever.getMessage();
			String message2 = String.valueOf(System.nanoTime());
			UserMessage m2 = new UserMessage(message2, "127.0.0.1",
					Network.CLIENT_PORT_NO, System.nanoTime());
			reciever.sendMessage(m2);
		}
		long time = 0;
		for (int i = 0; i < 5; i++) {
			try {
				inboxLock.acquire();
			} catch (Exception e) {
			}
			m = sender.getMessage();
			time = Long.valueOf(m.message);
		}
		System.out.println("roundtrip for 5 messages : "
				+ (System.nanoTime() - time) + " nanoseconds");
		reciever.shutdown();
		sender.shutdown();
	}

	public void startListnerAndReciever() {
		reciever = new Network(Network.SERVER_PORT_NO, inboxLock);
		sender = new Network(Network.CLIENT_PORT_NO, inboxLock);
		new Thread(reciever).start();
		new Thread(sender).start();
	}
}
