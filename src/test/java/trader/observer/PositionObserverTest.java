package trader.observer;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.strategy.TradingStrategyConfiguration;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.order.OrderStrategy;
import trader.entity.price.Price;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PositionObserverTest extends BaseObserverTest {


    private PositionObserver positionObserver;
    private EntryStrategy entryStrategyMock;
    private OrderStrategy orderStrategyMock;
    private TradingStrategyConfiguration configurationMock;
    private ExitStrategy exitStrategyMock;
    private Price priceMock;
    private Trade tradeMock;

    @Before
    public void setUp(){
        super.before();
        entryStrategyMock = mock(EntryStrategy.class);
        orderStrategyMock = mock(OrderStrategy.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        exitStrategyMock = mock(ExitStrategy.class);
        priceMock = mock(Price.class);
        tradeMock = mock(Trade.class);
        positionObserver = new PositionObserver(brokerGatewayMock, entryStrategyMock, orderStrategyMock, configurationMock, exitStrategyMock);
    }


    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullBrokerGateway_Exception(){
        new PositionObserver( null, entryStrategyMock, orderStrategyMock, configurationMock, exitStrategyMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullEntryStrategy_Exception(){
        new PositionObserver( brokerGatewayMock, null, orderStrategyMock, configurationMock, exitStrategyMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullOrderStrategy_Exception(){
        new PositionObserver( brokerGatewayMock, entryStrategyMock, null, configurationMock, exitStrategyMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatePositionObserverWithNullConfiguration_Exception(){
        new PositionObserver( brokerGatewayMock, entryStrategyMock, orderStrategyMock, null, exitStrategyMock);
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullExitStrategy_WhenCreatePositionObserver_ThenThrowException() {
        new PositionObserver( brokerGatewayMock, entryStrategyMock, orderStrategyMock, configurationMock, null);
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

    @Test
    public void TestIfOrderStrategyIsSet(){
        Object orderStrategy = commonTestMembers.extractFieldObject(positionObserver, "orderStrategy");

        assertNotNull(commonTestMembers.extractFieldObject(positionObserver, "orderStrategy"));
        assertEquals(orderStrategyMock, orderStrategy);
    }

    @Test
    public void TestIfTradingConfigurationIsSet(){
        Object configuration = commonTestMembers.extractFieldObject(positionObserver, "configuration");

        assertNotNull(commonTestMembers.extractFieldObject(positionObserver, "configuration"));
        assertEquals(configurationMock, configuration);
    }

    @Test
    public void TestIfExitStrategyIsSet(){
        Object exitStrategy = commonTestMembers.extractFieldObject(positionObserver, "exitStrategy");

        assertNotNull(commonTestMembers.extractFieldObject(positionObserver, "exitStrategy"));
        assertEquals(exitStrategyMock, exitStrategy);
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

    @Test(expected = BadRequestException.class)
    public void WhenCallUpdateObserverWithCorrectPriceAndNoOpenTradesOrOrdersAndWithEntrySignal_PlaceNewOrder(){
        BigDecimal askPrice = BigDecimal.valueOf(1.2000);
        BigDecimal entryPrice = BigDecimal.valueOf(1.2020);
        setZeroTradesAndOrders();
        when(entryStrategyMock.generateTrade()).thenReturn(tradeMock);
        when(priceMock.getAsk()).thenReturn(askPrice);
        setTradableTrade(Direction.UP, entryPrice);

        doThrow(new BadRequestException()).when(orderStrategyMock).placeTradeAsOrder(any(BrokerGateway.class),any(Price.class), any(Trade.class), any(TradingStrategyConfiguration.class));

        positionObserver.updateObserver(priceMock);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallUpdateObserverAndHaveOpenOrder_CallCloseUnfilledOrders(){
        when(brokerGatewayMock.totalOpenOrdersSize()).thenReturn(11);
        doThrow(new BadRequestException()).when(orderStrategyMock).closeUnfilledOrders(brokerGatewayMock, priceMock);

        positionObserver.updateObserver(priceMock);
    }

    @Test
    public void givenOpenTrades_WhenCallUpdateObserver_ThenCallExitStrategyExecute(){

        when(brokerGatewayMock.totalOpenTradesSize()).thenReturn(1);
        when(brokerGatewayMock.totalOpenOrdersSize()).thenReturn(0);
        positionObserver.updateObserver(priceMock);

        verify(exitStrategyMock, times(1)).execute(priceMock);

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
