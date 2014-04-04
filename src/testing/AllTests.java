package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestGameBoard.class       ,
	TestLog.class             ,
	TestEnd.class             ,
	TestEnemyExistance.class  ,
	TestGameBoard.class       ,
	TestLog.class             ,
	TestLose.class            ,
	TestPowerUp.class         ,
	TestTouching.class        ,
	TestWin.class             ,
	TestLatency.class         ,
	TestPacketLoss.class
})
public class AllTests {

}
