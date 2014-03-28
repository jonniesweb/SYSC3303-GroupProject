package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestGameBoard.class       ,
	TestLog.class             , 
	AllTests.class            ,
	TestEnd.class             ,
	TestEnemyExistance.class  ,
	TestGameBoard.class       ,
	TestLog.class             ,
	TestLose.class            ,
	TestPowerUp.class         ,
	TestTouching.class        ,
	TestWin.class
})
public class AllTests {

}
