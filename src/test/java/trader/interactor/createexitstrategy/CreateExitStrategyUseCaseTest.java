package trader.interactor.createexitstrategy;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.exit.halfclosetrail.HalfCloseTrailExitStrategy;
import trader.interactor.createexitstrategy.CreateExitStrategyUseCase;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateExitStrategyUseCaseTest {

    private UseCase createExitStrategyUseCase;
    private Request requestMock;

    @Before
    public void setUp() throws Exception {
        createExitStrategyUseCase = new CreateExitStrategyUseCase();
        requestMock = mock(Request.class);
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

    private void setFakeSettings(String keyName, String strategyName) {
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put(keyName, strategyName);
        inputSettings.put("settings", settings);
        when(requestMock.getBody()).thenReturn(inputSettings);
    }

}
