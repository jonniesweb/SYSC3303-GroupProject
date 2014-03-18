package Networking;

import java.net.DatagramPacket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

/**
 * A representation of a message received from a user, or to send to a User
 *
 */
public class UserMessage extends Message {
	public DatagramPacket datagram;
	public InetAddress ip;
	public int port;
	public long time;
	
	private static final Logger LOG = Logger.getLogger(
            UserMessage.class.getName());

	/**
	 * Create a UserMessage from a Datagram received from the User
	 * @param p
	 */
	public UserMessage(DatagramPacket p) {
		super(new String(p.getData()).trim());
		datagram = p;
		ip = p.getAddress();
		port = p.getPort();
	}
	
	/**
	 * Create a UserMessage to send to someone
	 * @param message
	 * @param hostName
	 * @param port
	 * @param time
	 */
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
			LOG.error(e);
		}
		datagram = new DatagramPacket(message.getBytes(), message.getBytes().length, ip, port);
	}
}
