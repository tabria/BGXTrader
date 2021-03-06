package trader.strategy;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.ThreadedStrategy;
import trader.exception.NullArgumentException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ThreadedStrategyTest {

    private CommonTestClassMembers commonMembers;
    private Strategy strategy;

    @Before
    public void setUp() throws Exception {
        commonMembers = new CommonTestClassMembers();
        strategy = mock(Strategy.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatingWithNull_Exception(){
        new ThreadedStrategy(null);
    }

    @Test
    public void threadMustBeStartedAfterObjectCreation(){
        int oldSize = Thread.getAllStackTraces().size();
        ThreadedStrategy threadedStrategy = new ThreadedStrategy(strategy);
        int newSize =Thread.getAllStackTraces().size();

        assertEquals(oldSize+1, newSize);
    }

    @Test(expected = ExecuteCalledException.class)
    public void WhenThreadStartedThenCallExecute() throws InterruptedException {
        ThreadedStrategy threadedStrategy = new ThreadedStrategy(strategy);
        doThrow(new ExecuteCalledException()).when(strategy).execute();
        threadedStrategy.run();
    }


private class ExecuteCalledException extends RuntimeException{};

}
