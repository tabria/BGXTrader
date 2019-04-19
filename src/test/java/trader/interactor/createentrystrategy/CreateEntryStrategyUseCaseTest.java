package trader.interactor.createentrystrategy;

import org.junit.Before;
import org.junit.Test;
import trader.entry.standard.StandardEntryStrategy;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.interactor.BaseStrategyTest;
import trader.requestor.UseCase;
import trader.responder.Response;
import static org.junit.Assert.assertEquals;

public class CreateEntryStrategyUseCaseTest extends BaseStrategyTest {


    private UseCase createEntryStrategyUseCase;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createEntryStrategyUseCase = new CreateEntryStrategyUseCase();
    }

    @Test(expected = NullArgumentException.class)
    public void givenBadEntryStrategyKeyName_WhenCallExecute_ThenThrowException() {
       setFakeSettings("entry", "standard");

       createEntryStrategyUseCase.execute(requestMock);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void givenEmptySettings_WhenCallExecute_ThenThrowException(){
        setFakeSettings("entryStrategy","   ");

        createEntryStrategyUseCase.execute(requestMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenReturnCorrectEntryStrategy(){
        setFakeSettings("entryStrategy", "standard ");

        Response response = createEntryStrategyUseCase.execute(requestMock);
        Object body = response.getBody();

        assertEquals(StandardEntryStrategy.class, body.getClass());
    }
}
