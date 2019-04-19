package trader.interactor.createorderservice;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.interactor.BaseStrategyTest;
import trader.interactor.createorderstrategy.CreateOrderStrategyUseCase;
import trader.order.standard.StandardOrderStrategy;
import trader.requestor.UseCase;
import trader.responder.Response;
import static org.junit.Assert.assertEquals;

public class CreateOrderStrategyUseCaseTest extends BaseStrategyTest {

    private UseCase createOrderStrategyUseCase;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createOrderStrategyUseCase = new CreateOrderStrategyUseCase();
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

}
