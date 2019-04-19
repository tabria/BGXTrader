package trader.interactor.createexitstrategy;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.exit.halfclosetrail.HalfCloseTrailExitStrategy;
import trader.interactor.BaseStrategyTest;
import trader.requestor.UseCase;
import trader.responder.Response;

import static org.junit.Assert.assertEquals;

public class CreateExitStrategyUseCaseTest extends BaseStrategyTest {

    private UseCase createExitStrategyUseCase;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        createExitStrategyUseCase = new CreateExitStrategyUseCase();

    }

    @Test(expected = NullArgumentException.class)
    public void givenBadOrderStrategyKeyName_WhenCallExecute_ThenThrowException() {
        setFakeSettings("entry", "halfCloseTrail");

        createExitStrategyUseCase.execute(requestMock);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void givenEmptySettings_WhenCallExecute_ThenThrowException(){
        setFakeSettings("exitStrategy","   ");

        createExitStrategyUseCase.execute(requestMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenReturnCorrectOrderStrategy(){
        setFakeSettings("exitStrategy", "halfCloseTrail ");

        Response response = createExitStrategyUseCase.execute(requestMock);
        Object body = response.getBody();

        assertEquals(HalfCloseTrailExitStrategy.class, body.getClass());
    }

}
