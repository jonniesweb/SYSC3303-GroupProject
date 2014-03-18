package Networking;

/**
 * A message that can be handled by the LogicManager
 * 
 */
public class Message {

	public String message;

	public Message(String message) {
		this.message = message;
	}

	/**
	 * Returns a string representation of the message.
	 * 
	 * @return
	 */
	public String getData() {
		return message;
	}

}