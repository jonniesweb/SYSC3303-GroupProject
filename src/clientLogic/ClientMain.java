package clientLogic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import org.apache.log4j.Logger;

import Networking.Message;
import Networking.Network;
import Networking.UserMessage;

public class ClientMain extends SpectatorMain {
	
	private static final Logger LOG = Logger.getLogger(
			ClientMain.class.getName());

	public static void main(String[] args) {
		Random r = new Random();
		int port = 8000 + r.nextInt(100);
		new ClientMain(port);

	}
	
	/**
	 * Starts up a Client with the specified port #
	 * @param port
	 */
	public ClientMain(int port) {
		super(port, "127.0.0.1", Network.SERVER_PORT_NO);
		this.view.guiFrame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					sendMessage("UP");
					break;
					
				case KeyEvent.VK_A:
					sendMessage("LEFT");
					break;
					
				case KeyEvent.VK_S:
					sendMessage("DOWN");
					break;
					
				case KeyEvent.VK_D:
					sendMessage("RIGHT");
					break;
					
				case KeyEvent.VK_SPACE:
					sendMessage("BOMB");
					break;
					
				case KeyEvent.VK_J:
					sendMessage("JOIN_GAME");
					break;
					
				case KeyEvent.VK_K:
					sendMessage("END_GAME");
					break;
					
				case KeyEvent.VK_ENTER:
					sendMessage("START_GAME");
					break;
					
				case KeyEvent.VK_SLASH:
					sendMessage("RESET_LIFE");
				default:
					break;
				}
			}
		});
	}
	
	/**
	 * Send a string to the server
	 * @param data
	 */
	void sendMessage(String data) {
		network.sendMessage(new UserMessage(data, "127.0.0.1", Network.SERVER_PORT_NO, System.nanoTime()));
		//System.out.println("send command: " + data);
		LOG.info("SEND COMMAND : " + data);
	}

}
