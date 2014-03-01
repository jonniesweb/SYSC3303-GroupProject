package clientLogic;

import entities.Entity;
import gameLogic.GameBoard;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;

public class ClientGUIFrame extends JFrame {

	int width;
	int height;
	JButton[][] buttonGameBoard;

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUIFrame frame = new ClientGUIFrame(10, 10,
							new GameBoard(10, 10)); // XXX: constructor call for
													// testing only
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientGUIFrame(int width, int height, GameBoard gameBoard) {
		this.width = width;
		this.height = height;

		// GUI configuration
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));

		// gameboard configuration
		update(gameBoard);
	}

	void update(GameBoard gameBoard) {
		Entity[][] entityArray = gameBoard.getBoard();

		for (int i = 0; i < entityArray.length; i++) {
			for (int j = 0; j < entityArray[i].length; j++) {
				Entity entity = entityArray[i][j];

			}
		}

	}

}
