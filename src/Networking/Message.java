package Networking;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Message {
	public DatagramPacket datagram;
	public InetAddress ip;
	public int packetPort;

	public Message(DatagramPacket p) {
		datagram = p;
		ip = p.getAddress();
		packetPort = p.getPort();
	}
	
	public Message(String m, String hostName, int p) {
		this.packetPort = p;
		try {
			ip = InetAddress.getByName(hostName);
		} catch (Exception e) {
			System.out.println(e);
		}
		datagram = new DatagramPacket(m.getBytes(), m.getBytes().length, ip,
				packetPort);
	}
}