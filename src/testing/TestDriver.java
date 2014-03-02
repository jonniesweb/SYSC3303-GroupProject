package testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.String;
import java.util.concurrent.Semaphore;
import Networking.*;


public class TestDriver implements Runnable {

	String file;
	String[] list = new String[100];
	int count = 0;
	String serverIp = "127.0.0.1";
	int serverPort = Network.SEVER_PORT_NO;

	public TestDriver(String filename) {
		this.file = filename;
	}

	public void runTest() {

		BufferedReader in = null;
		/*
		 * read input from file
		 */
		try {
			in = new BufferedReader(new FileReader(file));

			String read = null;
			while ((read = in.readLine()) != null) {
				list[count++] = read.trim();
			}
			for (int i = 0; i < count; i++) {
				System.out.println(list[i]);
			}
		} catch (Exception e) {
			System.out.println("There was a problem: " + e);
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				System.out.println("There was a problem: " + e);
				e.printStackTrace();
			}
		}

		/*
		 * send list of test messages to server
		 */
		Network net = new Network(Network.CLIENT_PORT_NO, new Semaphore(0));
		for (String command : list) {
			net.sendMessage(new Message(command,serverIp,serverPort));
		}
	}

	public void run() {

		runTest();

	}

}
