package Networking;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UserMessage extends Message {
	public DatagramPacket datagram;
	public InetAddress ip;
	public int port;
	public long time;

	public UserMessage(DatagramPacket p) {
		super(new String(p.getData()).trim());
		datagram = p;
		ip = p.getAddress();
		port = p.getPort();
	}
	
	public UserMessage(String message, String hostName, int port, long time) {
		super(message);
		this.time = time;
		this.port = port;
		this.message = message;
		
		// TODO: Remove dependency on having localhost as the server
		hostName = "127.0.0.1";
		try {
			ip = InetAddress.getByName(hostName);
		} catch (Exception e) {
			System.out.println(e);
		}
		datagram = new DatagramPacket(message.getBytes(), message.getBytes().length, ip, port);
	}
}
