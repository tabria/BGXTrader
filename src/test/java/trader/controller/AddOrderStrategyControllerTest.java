package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.responder.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddOrderStrategyControllerTest extends BaseControllerTest {

    private TraderController<OrderStrategy> controller;
    private OrderStrategy orderStrategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        orderStrategy = mock(OrderStrategy.class);
        controller = new AddOrderStrategyController<>(requestBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddOrderStrategyController<>(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUseCaseFactory_Exception(){
        new AddOrderStrategyController<>(requestBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){

        setExecuteSettings("AddOrderStrategyController");
        when(responseMock.getResponseDataStructure()).thenReturn(orderStrategy);
        Response<OrderStrategy> response = controller.execute(settings);

        assertEquals(orderStrategy, response.getResponseDataStructure());
    }

}
