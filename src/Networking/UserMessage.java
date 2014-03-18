package Networking;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UserMessage {
	public DatagramPacket datagram;
	public String message;
	public InetAddress ip;
	public int port;
	public long time;

	public UserMessage(DatagramPacket p) {
		datagram = p;
		ip = p.getAddress();
		port = p.getPort();
		message = new String(datagram.getData()).trim();
	}
	
	public UserMessage(String message, String hostName, int port, long time) {
		this.time = time;
		this.port = port;
		this.message = message;
		hostName = "127.0.0.1";
		try {
			ip = InetAddress.getByName(hostName);
		} catch (Exception e) {
			System.out.println(e);
		}
		datagram = new DatagramPacket(message.getBytes(), message.getBytes().length, ip, port);
	}
	
	/**
	 * Returns a String of the data of the message. Calls the trim() method on
	 * the string to remove extraneous whitespace.
	 * @return
	 */
	public String getData() {
		return message;
	}
}
