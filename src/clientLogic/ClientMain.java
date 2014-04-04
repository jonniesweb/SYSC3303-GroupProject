package clientLogic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;

import Networking.Network;


public class ClientMain extends SpectatorMain {
	
	public static void main(String[] args) {
		new ClientMain();

	}
	
	/**
	 * Starts up a Client with the specified port #
	 * @param port
	 */
	public ClientMain() {
		super();
		
		LOG = Logger.getLogger(ClientMain.class.getName());
		
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
	
	@Override
	protected void startServer() {
		// setup network
		network = new Network(clientPort, inboxLock);
		new Thread(network).start();
		view.guiFrame.setVisible(true);

		// connect to server
		startListening();

		sendMessage("JOIN_GAME");
	}

	
	
}
