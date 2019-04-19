package trader.interactor.createorderservice;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.interactor.createorderstrategy.CreateOrderStrategyUseCase;
import trader.order.standard.StandardOrderStrategy;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateOrderStrategyUseCaseTest {

    private UseCase createOrderStrategyUseCase;
    private Request requestMock;

    @Before
    public void setUp() throws Exception {
        createOrderStrategyUseCase = new CreateOrderStrategyUseCase();
        requestMock = mock(Request.class);
    }

    @Test(expected = NullArgumentException.class)
    public void givenBadOrderStrategyKeyName_WhenCallExecute_ThenThrowException() {
        setFakeSettings("entry", "standard");

        createOrderStrategyUseCase.execute(requestMock);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void givenEmptySettings_WhenCallExecute_ThenThrowException(){
        setFakeSettings("orderStrategy","   ");

        createOrderStrategyUseCase.execute(requestMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenReturnCorrectOrderStrategy(){
        setFakeSettings("orderStrategy", "standard ");

        Response response = createOrderStrategyUseCase.execute(requestMock);
        Object body = response.getBody();

        assertEquals(StandardOrderStrategy.class, body.getClass());
    }

    private void setFakeSettings(String keyName, String strategyName) {
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put(keyName, strategyName);
        inputSettings.put("settings", settings);
        when(requestMock.getBody()).thenReturn(inputSettings);
    }

}
