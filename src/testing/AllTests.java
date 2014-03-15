package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClientGUIFrameTest.class, TestGameBoard.class, TestLog.class })
public class AllTests {

}
