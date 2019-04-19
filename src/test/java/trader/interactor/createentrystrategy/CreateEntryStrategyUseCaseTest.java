package trader.interactor.createentrystrategy;

import org.junit.Before;
import org.junit.Test;
import trader.entry.standard.StandardEntryStrategy;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateEntryStrategyUseCaseTest {


    private UseCase createEntryStrategyUseCase;
    private Request requestMock;

    @Before
    public void setUp() throws Exception {
        createEntryStrategyUseCase = new CreateEntryStrategyUseCase();
        requestMock = mock(Request.class);
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

    private void setFakeSettings(String keyName, String strategyName) {
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put(keyName, strategyName);
        inputSettings.put("settings", settings);
        when(requestMock.getBody()).thenReturn(inputSettings);
    }
}
