package clientLogic;

import entities.Bomb;
import entities.Door;
import entities.Enemy;
import entities.Entity;
import entities.Explosion;
import entities.Player;
import entities.PowerUp;
import entities.Wall;
import gameLogic.GameBoard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


// TODO: fix testing of client because it crashes
public class ClientGUIFrame extends JFrame {

	private int width = 0;
	private int height = 0;
	private JButton[][] buttonGameBoard;

	// define images
	private Image bombImg;
	private Image doorImg;
	private Image enemyImg;
	private Image floorImg;
	private Image explosionImg;
	private Image playerImg;
	private Image powerupImg;
	private Image wallImg;

	private JPanel contentPane;
	private GridLayout layoutManager = new GridLayout();
	
	public ClientGUIFrame() {
		setupContentPane();
		
		
	}

	/**
	 * Create the frame with the specified gameboard.
	 * 
	 * @param gameBoard
	 */
	public ClientGUIFrame(GameBoard gameBoard) {
		this.width = gameBoard.getWidth();
		this.height = gameBoard.getHeight();

		buttonGameBoard = new JButton[width][height];
		

		setupContentPane();

		/*
		 *  initialize gameboard with JButtons that have 'X' on them and put
		 *  them into the contentPane
		 */
		for (int i = 0; i < buttonGameBoard.length; i++) {
			for (int j = 0; j < buttonGameBoard[i].length; j++) {
				buttonGameBoard[i][j] = new JButton("X");
				setButton(buttonGameBoard[i][j]);
			}
		}
		

		// gameboard configuration
		update(gameBoard);

	}

	private void setupContentPane() {
		// init images for the gameboard JButtons
		// commented out since images havent been graphically designed yet
		// initImages();

		// GUI configuration
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 450);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(layoutManager);
	}

	/**
	 * Creates a 10x10 gameboard for very quick testing
	 * 
	 * @return
	 */

	public static GameBoard testData() {
		GameBoard board = new GameBoard(10, 10);
		board.randomizeFloor(2);
		return board;

	}

	/**
	 * Helper method to quickly put the given JButton into the given x,y
	 * position on the gameboard. Should only be used for setting up
	 * the gameboard.
	 * 
	 * @param button
	 */
	private void setButton(JButton button) {
		contentPane.add(button);
		button.setFocusable(false);
	}

	/**
	 * Helper method to load all images for the buttons
	 * 
	 * @return
	 */
	private boolean initImages() {
		try {
			// initialize icons for buttons
			bombImg = ImageIO
					.read(getClass().getResource("resources/bomb.bmp"));
			doorImg = ImageIO
					.read(getClass().getResource("resources/door.bmp"));
			enemyImg = ImageIO.read(getClass().getResource(
					"resources/enemy.bmp"));
			floorImg = ImageIO.read(getClass().getResource(
					"resources/floor.bmp"));
			explosionImg = ImageIO.read(getClass().getResource(
					"resources/explosion.bmp"));
			playerImg = ImageIO.read(getClass().getResource(
					"resources/player.bmp"));
			powerupImg = ImageIO.read(getClass().getResource(
					"resources/powerup.bmp"));
			wallImg = ImageIO
					.read(getClass().getResource("resources/wall.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	/**
	 * updates and draws the GUI gameboard with the current gameboard state
	 * 
	 * @param gameBoard
	 */
	public void update(GameBoard gameBoard) {
		
		if(gameBoard.getHeight() != height || gameBoard.getWidth() != width) {
			
			// remove all components from the frame since theres an update
			contentPane.removeAll();
			
			// set width and height
			width = gameBoard.getWidth();
			height= gameBoard.getHeight();
			
			// Tell the gridlayout to set this new width and height
			layoutManager.setColumns(width);
			layoutManager.setRows(height);
			
			// fill the buttonGameboard with new buttons since the size has changed
			buttonGameBoard = new JButton[width][height];
			for (int i = 0; i < buttonGameBoard.length; i++) {
				for (int j = 0; j < buttonGameBoard[i].length; j++) {
					buttonGameBoard[j][i] = new JButton("X");
					setButton(buttonGameBoard[j][i]);
				}
			}
		}

		System.out.println("called GUI update");
		Entity[][] entityArray = gameBoard.getBoard();

		for (int i = 0; i < entityArray.length; i++) {
			for (int j = 0; j < entityArray[i].length; j++) {
				Entity entity = entityArray[i][j];
				JButton button = buttonGameBoard[i][j];

				if (entity instanceof Player) {
					// button = new JButton(new ImageIcon(playerImg));
					button.setText("@");
				} else if (entity instanceof Bomb) {
					// button = new JButton(new ImageIcon(bombImg));
					button.setText("B");
				} else if (entity instanceof Door) {
					// button = new JButton(new ImageIcon(doorImg));
					button.setText("D");
				} else if (entity instanceof Enemy) {
					// button = new JButton(new ImageIcon(enemyImg));
					button.setText("E");
				} else if (entity instanceof Explosion) {
					// button = new JButton(new ImageIcon(explosionImg));
					button.setText("X");
				} else if (entity instanceof PowerUp) {
					// button = new JButton(new ImageIcon(powerupImg));
					button.setText("P");
				} else if (entity instanceof Wall) {
					// button = new JButton(new ImageIcon(wallImg));
					button.setText("W");
//					button.setBackground(new Color(255, 0, 0));
				} else {
					// button = new JButton(new ImageIcon(floorImg));
					button.setText(" ");
//					button.setBackground(new Color(0, 0, 255));
				}

//				setButton(button, i, j); // XXX
			}
		}

		// set minimum size for frame
		int minTileSize = 50; // set to this because it doesn't show the text properly if too small
		setMinimumSize(new Dimension(minTileSize * width, minTileSize * height));
		pack();
	}

}
