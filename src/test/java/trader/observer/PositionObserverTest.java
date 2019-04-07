package trader.observer;

import org.junit.Before;
import org.junit.Test;
import trader.controller.AddIndicatorController;
import trader.controller.AddTradeController;
import trader.controller.TraderController;
import trader.exception.NullArgumentException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PositionObserverTest extends BaseObserverTest {


    private PositionObserver positionObserver;
    private TraderController<Object> addTradeControllerMock;

    @Before
    public void setUp(){
        super.before();
        addTradeControllerMock = mock(AddIndicatorController.class);
        positionObserver = new PositionObserver(mockConfiguration, brokerGatewayMock);

    }


    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullBrokerGateway_Exception(){
        new PositionObserver(mockConfiguration, null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullConfiguration_Exception(){
        new PositionObserver(null, brokerGatewayMock);
    }

    @Test
    public void TestIfBrokerGatewayIsSet(){
        assertEquals(brokerGatewayMock, commonTestMembers.extractFieldObject(positionObserver, "brokerGateway"));
    }

    @Test
    public void TestIfConfigurationIsSet(){
        assertEquals(mockConfiguration, commonTestMembers.extractFieldObject(positionObserver, "configuration"));
    }

    @Test
    public void TestIfTradeControllerIsSet(){
        Object addTradeController = commonTestMembers.extractFieldObject(positionObserver, "addTradeController");

        assertNotNull(commonTestMembers.extractFieldObject(positionObserver, "addTradeController"));
        assertEquals(AddTradeController.class, addTradeController.getClass() );
    }



    @Test
    public void WhenNoOpenTradesAndNoOpenOrders_CallTradeController(){
        when(brokerGatewayMock.totalOpenTradesSize()).thenReturn(0);
        when(brokerGatewayMock.totalOpenOrdersSize()).thenReturn(0);

    }

}
