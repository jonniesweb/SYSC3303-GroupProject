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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;


// TODO: fix testing of client because it crashes
public class ClientGUIFrame extends JFrame {

	private int width = 0;
	private int height = 0;
	private int tileSize = 50; // change this to change the tile size
	private JButton[][] buttonGameBoard;

	// define images
	private ImageIcon bombImg;
	private ImageIcon doorImg;
	private ImageIcon enemyImg;
	private ImageIcon floorImg;
	private ImageIcon explosionImg;
	private ImageIcon playerImg;
	private ImageIcon powerupImg;
	private ImageIcon wallImg;

	private JPanel contentPane;
	private GridLayout layoutManager = new GridLayout();
	
	private static final Logger LOG = Logger.getLogger(
			ClientGUIFrame.class.getName());
	
	public ClientGUIFrame() {
		initImages();
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
		
		initImages();
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
			
			BufferedImage tiles = ImageIO.read(new File("res/TilesMisc.png"));
			
			
			// initialize icons for buttons
			floorImg = new ImageIcon(tiles.getSubimage(0, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
			wallImg = new ImageIcon(tiles.getSubimage(20, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
			explosionImg = new ImageIcon(tiles.getSubimage(40, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
			powerupImg = new ImageIcon(tiles.getSubimage(60, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
			bombImg = new ImageIcon(tiles.getSubimage(80, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
			doorImg = new ImageIcon(tiles.getSubimage(100, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
			enemyImg = new ImageIcon(tiles.getSubimage(120, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
			playerImg = new ImageIcon(tiles.getSubimage(140, 0, 20, 20).getScaledInstance(tileSize, tileSize, Image.SCALE_FAST));
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
					buttonGameBoard[j][i] = new ButtonOnDrugs();
					setButton(buttonGameBoard[j][i]);
				}
			}
		}

		//System.out.println("called GUI update");
		LOG.info("GUI UPDATE CALL");
		Entity[][] entityArray = gameBoard.getBoard();

		for (int i = 0; i < entityArray.length; i++) {
			for (int j = 0; j < entityArray[i].length; j++) {
				Entity entity = entityArray[i][j];
				JButton button = buttonGameBoard[i][j];

				if (entity instanceof Player) {
					button.setIcon(playerImg);
				} else if (entity instanceof Bomb) {
					button.setIcon(bombImg);
				} else if (entity instanceof Door) {
					button.setIcon(doorImg);
				} else if (entity instanceof Enemy) {
					button.setIcon(enemyImg);
				} else if (entity instanceof Explosion) {
					button.setIcon(explosionImg);
				} else if (entity instanceof PowerUp) {
					button.setIcon(powerupImg);
				} else if (entity instanceof Wall) {
					button.setIcon(wallImg);
				} else {
					button.setIcon(floorImg);
				}

			}
		}

		// set minimum size for frame
		setMinimumSize(new Dimension(tileSize * width, tileSize * height));
		pack();
	}
	
	class ButtonOnDrugs extends JButton {
		
		public ButtonOnDrugs() {
			setStuff();
		}
		
		public ButtonOnDrugs(Icon icon) {
			super(icon);
			setStuff();
		}
		
		private void setStuff() {
			setBorder(BorderFactory.createEmptyBorder());
			setContentAreaFilled(false);
			
		}
		
	}

}
