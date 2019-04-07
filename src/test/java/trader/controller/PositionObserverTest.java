package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.exception.NullArgumentException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PositionObserverTest {

    private BrokerGateway brokerGatewayMock;
    private PositionObserver positionObserver;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp(){

        brokerGatewayMock = mock(BrokerGateway.class);
        positionObserver = new PositionObserver(brokerGatewayMock);
        commonMembers = new CommonTestClassMembers();

    }


    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullBrokerGateway_Exception(){
        new PositionObserver(null);
    }

    @Test
    public void TestIfBrokerGatewayIsSet(){
        assertEquals(brokerGatewayMock, positionObserver.getBrokerGateway());
    }

    @Test
    public void WhenNoOpenTradesAndNoOpenOrders_CallTradeController(){
        when(brokerGatewayMock.totalOpenTradesSize()).thenReturn(0);
        when(brokerGatewayMock.totalOpenOrdersSize()).thenReturn(0);
    }

}
