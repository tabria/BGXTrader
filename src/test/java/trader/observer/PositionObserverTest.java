package trader.observer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Null;
import trader.controller.CreateIndicatorController;
import trader.controller.TraderController;
import trader.entity.trade.Trade;
import trader.exception.NullArgumentException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PositionObserverTest extends BaseObserverTest {


    private PositionObserver positionObserver;
    private TraderController<Trade> addTradeControllerMock;

    @Before
    public void setUp(){
        super.before();
        addTradeControllerMock = mock(CreateIndicatorController.class);
        positionObserver = new PositionObserver(brokerGatewayMock, addTradeControllerMock);
    }


    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullBrokerGateway_Exception(){
        new PositionObserver( null, addTradeControllerMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullTradeController_Exception(){
        new PositionObserver( brokerGatewayMock, null);
    }

    @Test
    public void TestIfBrokerGatewayIsSet(){
        assertEquals(brokerGatewayMock, commonTestMembers.extractFieldObject(positionObserver, "brokerGateway"));
    }

    @Test
    public void TestIfTradeControllerIsSet(){
        Object addTradeController = commonTestMembers.extractFieldObject(positionObserver, "addTradeController");

        assertNotNull(commonTestMembers.extractFieldObject(positionObserver, "addTradeController"));
        assertEquals(addTradeControllerMock, addTradeController);
    }



    @Test
    public void WhenNoOpenTradesAndNoOpenOrders_CallTradeController(){
        when(brokerGatewayMock.totalOpenTradesSize()).thenReturn(0);
        when(brokerGatewayMock.totalOpenOrdersSize()).thenReturn(0);

    }

}
