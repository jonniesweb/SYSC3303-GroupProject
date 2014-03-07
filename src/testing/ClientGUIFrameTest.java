/**
 * 
 */
package testing;

import static org.junit.Assert.*;
import gameLogic.GameBoard;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import serverLogic.UserManager;

/**
 * @author jonsimpson
 *
 */
public class ClientGUIFrameTest {
	
	UserManager userManager = new UserManager();
	GameBoard gameBoard = new GameBoard(7,7);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link clientLogic.ClientGUIFrame#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link clientLogic.ClientGUIFrame#ClientGUIFrame(gameLogic.GameBoard)}.
	 */
	@Test
	public void testClientGUIFrame() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link clientLogic.ClientGUIFrame#update(gameLogic.GameBoard)}.
	 */
	@Test
	public void testUpdateGameBoard() {
		fail("Not yet implemented");
	}

}
