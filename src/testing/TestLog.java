package testing;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class TestLog {
	BufferedReader in;

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Logger log = new Logger();
		in = null;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAcceptMessage() throws Exception {
		setUp();
		//First log
		Logger.acceptMessage("UserMessage written");
		assertEquals(Logger.getLog().get(0),("UserMessage written"));
		//Second log
		//Check if it is queue properly
		Logger.acceptMessage("Again message written");
		assertEquals(Logger.getLog().get(1),("Again message written"));
		Logger.writeLog();
		Logger.endLog();
		in = new BufferedReader(new FileReader("Log.txt"));
		//assertEquals(in.readLine(),"UserMessage writtend");
	}
	
	@Test
	public void testWriteLog() throws Exception {
		setUp();
		//First UserMessage write to file
		Logger.acceptMessage("UserMessage written");
		Logger.writeLog();
		Logger.acceptMessage("Again message written");
		Logger.writeLog();
		Logger.endLog();
		in = new BufferedReader(new FileReader("Log.txt"));
		
		//check if all message accepted properly
		assertEquals(in.readLine(),"UserMessage written");
		assertEquals(in.readLine(),"Again message written");
			
	}
	

}
