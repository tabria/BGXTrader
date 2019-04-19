package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.order.OrderStrategy;
import trader.requestor.*;
import trader.responder.Response;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CreateOrderStrategyControllerTest extends BaseControllerTest {

    private static final String CREATE_ORDER_STRATEGY_CONTROLLER = "CreateOrderStrategyController";

    private CreateOrderStrategyController<OrderStrategy> controller;
    private OrderStrategy orderStrategyMock;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        orderStrategyMock = mock(OrderStrategy.class);
        controller = new CreateOrderStrategyController<>(useCaseFactoryMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequestThenReturnCorrectRequest() {
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request request = controller.getRequest(CREATE_ORDER_STRATEGY_CONTROLLER, settings);

        assertEquals(requestMock, request);
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeUseCaseFactory();
        UseCase useCase = controller.make(CREATE_ORDER_STRATEGY_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenReturnCorrectResponse(){
        setFakes();
        setFakeResponseBody();

        Response<OrderStrategy> response = controller.execute(settings);
        assertEquals(orderStrategyMock, response.getBody());
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(orderStrategyMock);
    }

}
