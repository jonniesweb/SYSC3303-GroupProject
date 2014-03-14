package clientLogic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import Networking.Message;
import Networking.Network;

public class ClientMain extends SpectatorMain {

	public static void main(String[] args) {
		new ClientMain();

	}
	
	
	public ClientMain() {
		this.view.guiFrame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// check if 'W' key
				if (e.getKeyCode() == KeyEvent.VK_W) {
					network.sendMessage(new Message("UP", "127.0.0.1", Network.SEVER_PORT_NO, System.nanoTime()));
					
					// check if 'A' key
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					network.sendMessage(new Message("LEFT", "127.0.0.1", Network.SEVER_PORT_NO, System.nanoTime()));
					
					// check if 'S' key
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					network.sendMessage(new Message("DOWN", "127.0.0.1", Network.SEVER_PORT_NO, System.nanoTime()));
					
					// check if 'D' key
				} else if (e.getKeyCode() == KeyEvent.VK_D) {
					network.sendMessage(new Message("RIGHT", "127.0.0.1", Network.SEVER_PORT_NO, System.nanoTime()));
				}
			}
		});
	}

}
