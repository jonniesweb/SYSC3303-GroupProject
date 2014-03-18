package Networking;

import java.net.DatagramPacket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

public class Message {
	public DatagramPacket datagram;
	public String Message;
	public InetAddress ip;
	public int packetPort;
	public long time;
	private static final Logger LOG = Logger.getLogger(
            Message.class.getName());

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
			//System.out.println(e);
			LOG.error(e);
		}
		datagram = new DatagramPacket(message.getBytes(), message.getBytes().length, ip,
				packetPort);
	}
	
	/**
	 * Returns a String of the data of the message. Calls the trim() method on
	 * the string to remove extraneous whitespace.
	 * @return
	 */
	public String getData() {
		return new String(datagram.getData()).trim();
	}
}
