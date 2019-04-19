package trader.strategy.bgxstrategy.service;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private OrderStrategy orderStrategyMock;
    private Response responseMock;
    private UseCase useCaseMock;
    private UseCaseFactory useCaseFactoryMock;
    private OrderService service;

    @Before
    public void setUp() throws Exception {

        orderStrategyMock = mock(OrderStrategy.class);
        responseMock = mock(Response.class);
        useCaseMock = mock(UseCase.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        service = new OrderService(useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullOrderStrategyName_WhenCallCreateOrderStrategy_ThenThrowException(){
        service.createOrderStrategy(null);
    }

    @Test
    public void givenCorrectOrderStrategyName_WhenCallCreateOrderStrategy_ThenReturnCorrectResult(){
        setFakeOrderStrategy();
        OrderStrategy strategy = service.createOrderStrategy("standard");

        assertEquals(orderStrategyMock, strategy);
    }

    private void setFakeOrderStrategy() {
        when(responseMock.getBody()).thenReturn(orderStrategyMock);
        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
    }

}
