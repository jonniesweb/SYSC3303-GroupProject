package Networking;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Message {
	public DatagramPacket datagram;
	public String Message;
	public InetAddress ip;
	public int packetPort;
	public long time;

	public Message(DatagramPacket p) {
		datagram = p;
		ip = p.getAddress();
		packetPort = p.getPort();
	}
	
	public Message(String message, String hostName, int port, long time) {
		this.time = time;
		this.packetPort = port;
		hostName = "127.0.0.1";
		try {
			ip = InetAddress.getByName(hostName);
		} catch (Exception e) {
			System.out.println(e);
		}
		datagram = new DatagramPacket(message.getBytes(), message.getBytes().length, ip,
				packetPort);
	}
}
