package trader.observer;

import org.junit.Before;
import org.junit.Test;
import trader.controller.CreateIndicatorController;
import trader.controller.TraderController;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.price.Price;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PositionObserverTest extends BaseObserverTest {


    private PositionObserver positionObserver;
    private EntryStrategy entryStrategyMock;
    private Price priceMock;
    private Trade tradeMock;

    @Before
    public void setUp(){
        super.before();
        entryStrategyMock = mock(EntryStrategy.class);
        priceMock = mock(Price.class);
        tradeMock = mock(Trade.class);
        positionObserver = new PositionObserver(brokerGatewayMock, entryStrategyMock);
    }


    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullBrokerGateway_Exception(){
        new PositionObserver( null, entryStrategyMock);
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
    public void TestIfEntryStrategyIsSet(){
        Object entryStrategy = commonTestMembers.extractFieldObject(positionObserver, "entryStrategy");

        assertNotNull(commonTestMembers.extractFieldObject(positionObserver, "entryStrategy"));
        assertEquals(entryStrategyMock, entryStrategy);
    }

    @Test(expected = BadRequestException.class)
    public void WhenNoOpenTradesAndNoOpenOrders_CallEntryStrategy(){
        setZeroTradesAndOrders();
        when(entryStrategyMock.generateTrade()).thenThrow(new BadRequestException());

        positionObserver.updateObserver(priceMock);
    }

    @Test(expected = BadRequestException.class)
    public void CheckForLongIfPriceIsMoreThanTradableTresholdAboveTradeEntryPrice_ChangeTradeToNotTradable(){
        BigDecimal askPrice = BigDecimal.valueOf(1.2021);
        BigDecimal entryPrice = BigDecimal.valueOf(1.2000);
        setZeroTradesAndOrders();
        when(entryStrategyMock.generateTrade()).thenReturn(tradeMock);
        when(priceMock.getAsk()).thenReturn(askPrice);
        setTradableTrade(Direction.UP, entryPrice);
        doThrow(new BadRequestException()).when(tradeMock).setTradable(anyString());

        positionObserver.updateObserver(priceMock);
    }

    @Test(expected = BadRequestException.class)
    public void CheckForShortIfPriceIsLessThanTradableThresholdBelowTradeEntryPrice_ChangeTradeToNotTradable(){
        BigDecimal bidPrice = BigDecimal.valueOf(1.2000);
        BigDecimal entryPrice = BigDecimal.valueOf(1.2021);
        setZeroTradesAndOrders();
        when(entryStrategyMock.generateTrade()).thenReturn(tradeMock);
        when(priceMock.getBid()).thenReturn(bidPrice);
        setTradableTrade(Direction.DOWN, entryPrice);
        doThrow(new BadRequestException()).when(tradeMock).setTradable(anyString());

        positionObserver.updateObserver(priceMock);
    }

    @Test
    public void CheckIfTradeIsNotTradableDoNotSetTradableForThreshold(){
        setZeroTradesAndOrders();
        when(entryStrategyMock.generateTrade()).thenReturn(tradeMock);
        when(tradeMock.getTradable()).thenReturn(false);
        doThrow(new BadRequestException()).when(tradeMock).setTradable(anyString());

        positionObserver.updateObserver(priceMock);
    }

    @Test
    public void CheckIfTradeDirectionIsFlatDoNotSetTradableForThreshold(){
        setZeroTradesAndOrders();
        when(entryStrategyMock.generateTrade()).thenReturn(tradeMock);
        when(tradeMock.getTradable()).thenReturn(true);
        when(tradeMock.getDirection()).thenReturn(Direction.FLAT);
        doThrow(new BadRequestException()).when(tradeMock).setTradable(anyString());

        positionObserver.updateObserver(priceMock);
    }

    private void setZeroTradesAndOrders(){
        when(brokerGatewayMock.totalOpenTradesSize()).thenReturn(0);
        when(brokerGatewayMock.totalOpenOrdersSize()).thenReturn(0);
    }

    private void setTradableTrade(Direction direction, BigDecimal entryPrice){
        when(tradeMock.getTradable()).thenReturn(true);
        when(tradeMock.getDirection()).thenReturn(direction);
        when(tradeMock.getEntryPrice()).thenReturn(entryPrice);
    }

}
