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
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// XXX: constructor call for testing only
					GameBoard gameBoard = new GameBoard(10, 10);
					gameBoard.randomizeFloor(2);
					final ClientGUIFrame frame = new ClientGUIFrame(gameBoard);
					frame.setVisible(true);

					frame.update(testData()); // XXX: test

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ClientGUIFrame() {
		setupContentPane();
		
		
	}

	/**
	 * Create the frame.
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
				setButton(buttonGameBoard[i][j], i, j);
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
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);
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

//		Random r = new Random();
//
//		for (int i = 0; i < 10; i++) {
//			for (int j = 0; j < 10; j++) {
//				if (r.nextInt(10) < 7) {
//					board.getBoard()[i][j] = new Wall();
//				} else {
//					board.getBoard()[i][j] = new Entity();
//				}
//			}
//		}

	}

	/**
	 * Helper method to quickly put the given JButton into the given x,y
	 * position on the gameboard. Should only be used for setting up
	 * the gameboard.
	 * 
	 * @param button
	 * @param x
	 * @param y
	 */
	private void setButton(JButton button, int x, int y) {
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = x;
		gbc_btnNewButton.gridy = y;
		contentPane.add(button, gbc_btnNewButton);
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
			width = gameBoard.getWidth();
			height= gameBoard.getHeight();
			buttonGameBoard = new JButton[width][height];
			for (int i = 0; i < buttonGameBoard.length; i++) {
				for (int j = 0; j < buttonGameBoard[i].length; j++) {
					buttonGameBoard[i][j] = new JButton("X");
					setButton(buttonGameBoard[i][j], i, j);
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
					button.setText("Player");
				} else if (entity instanceof Bomb) {
					// button = new JButton(new ImageIcon(bombImg));
					button.setText("Bomb");
				} else if (entity instanceof Door) {
					// button = new JButton(new ImageIcon(doorImg));
					button.setText("Door");
				} else if (entity instanceof Enemy) {
					// button = new JButton(new ImageIcon(enemyImg));
					button.setText("Enemy");
				} else if (entity instanceof Explosion) {
					// button = new JButton(new ImageIcon(explosionImg));
					button.setText("Explosion");
				} else if (entity instanceof PowerUp) {
					// button = new JButton(new ImageIcon(powerupImg));
					button.setText("Powerup");
				} else if (entity instanceof Wall) {
					// button = new JButton(new ImageIcon(wallImg));
					button.setText("W");
					button.setBackground(new Color(255, 0, 0));
				} else {
					// button = new JButton(new ImageIcon(floorImg));
					button.setText("F");
					button.setBackground(new Color(0, 0, 255));
				}

//				setButton(button, i, j); // XXX
			}
		}

	}

}
