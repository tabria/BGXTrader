package trader.order;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.broker.connector.BrokerConnector;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.price.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderStrategyImplTest {

    private BrokerGateway brokerGatewayMock;
    private Trade tradeMock;
    private Price priceMock;
    private BrokerConnector connectorMock;
    private TradingStrategyConfiguration configurationMock;
    private OrderStrategyImpl orderStrategy;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {

        brokerGatewayMock = mock(BrokerGateway.class);
        tradeMock = mock(Trade.class);
        priceMock = mock(Price.class);
        connectorMock = mock(BrokerConnector.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        orderStrategy = new OrderStrategyImpl();
        commonMembers = new CommonTestClassMembers();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallPlaceTradeAsOrderWithNullBrokerGateway_Exception(){
        orderStrategy.placeTradeAsOrder(null, priceMock, tradeMock, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallPlaceTradeAsOrderWithNullPrice_Exception(){
        orderStrategy.placeTradeAsOrder(brokerGatewayMock, null, tradeMock, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallPlaceTradeAsOrderWithNullTrade_Exception(){
        orderStrategy.placeTradeAsOrder(brokerGatewayMock, priceMock, null, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallPlaceTradeAsOrderWithNullConfiguration_Exception(){
        orderStrategy.placeTradeAsOrder(brokerGatewayMock, priceMock, tradeMock, null);
    }


    @Test
    public void WhenCalculatePipValue_CorrectResult(){
        BigDecimal currentPrice = BigDecimal.valueOf(1.2000);
        when(priceMock.getBid()).thenReturn(currentPrice);
        BigDecimal pipValue = orderStrategy.getPipValue(priceMock);

        BigDecimal expected = BigDecimal.valueOf(0.0001).divide(currentPrice, 7, RoundingMode.HALF_UP);

        assertEquals(expected, pipValue);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCalculatePipValueWithZeroPrice_Exception(){
        BigDecimal currentPrice = BigDecimal.valueOf(0);
        when(priceMock.getBid()).thenReturn(currentPrice);
        orderStrategy.getPipValue(priceMock);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCalculatePipValueWithNullPrice_Exception(){
        orderStrategy.getPipValue(null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateStopSizeWithNullTrade_Exception(){
        orderStrategy.calculateStopSize(null);
    }

    @Test
    public void WhenCalCalculateStopSizeWithCorrectSettings_CorrectResult(){
        BigDecimal entryPrice = BigDecimal.valueOf(1.2000);
        BigDecimal stopLosPrice = BigDecimal.valueOf(1.1980);
        BigDecimal expectedStopSize = (entryPrice.subtract(stopLosPrice))
                .multiply(BigDecimal.valueOf(10_000));
        when(tradeMock.getEntryPrice()).thenReturn(entryPrice);
        when(tradeMock.getStopLossPrice()).thenReturn(stopLosPrice);

        BigDecimal stopSize = orderStrategy.calculateStopSize(tradeMock);

        assertTrue(stopSize.compareTo(expectedStopSize) == 0);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateTradeMarginWithNullBrokerGateway_Exception(){
        orderStrategy.calculateTradeMargin(null, BigDecimal.ONE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateTradeMarginWithNullUnitsSize_Exception(){
        orderStrategy.calculateTradeMargin(brokerGatewayMock, null);
    }

    @Test
    public void WhenCallCalculateTradeMarginWithCorrectSettings_CorrectResult(){
        when(connectorMock.getLeverage()).thenReturn("30");
        when(brokerGatewayMock.getConnector()).thenReturn(connectorMock);
        BigDecimal tradeMargin = orderStrategy.calculateTradeMargin(brokerGatewayMock, BigDecimal.ONE);

        assertEquals(0, BigDecimal.valueOf(0.03333).compareTo(tradeMargin));
    }

    @Test
    public void WhenCallCalculateTradeMarginWithZeroUnitSize_CorrectResult(){
        when(connectorMock.getLeverage()).thenReturn("30");
        when(brokerGatewayMock.getConnector()).thenReturn(connectorMock);
        BigDecimal tradeMargin = orderStrategy.calculateTradeMargin(brokerGatewayMock, BigDecimal.ZERO);

        assertEquals(0, BigDecimal.valueOf(0).compareTo(tradeMargin));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateUnitsSizeWithNullBrokerGateway_Exception(){
        orderStrategy.calculateUnitsSize(null, priceMock, tradeMock, configurationMock);
    }


    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateUnitsSizeWithNullPrice_Exception(){
        orderStrategy.calculateUnitsSize(brokerGatewayMock, null, tradeMock, configurationMock);
    }


    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateUnitsSizeWithNullTrade_Exception(){
        orderStrategy.calculateUnitsSize(brokerGatewayMock, priceMock, null, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateUnitsSizeWithNullConfiguration_Exception(){
        orderStrategy.calculateUnitsSize(brokerGatewayMock, priceMock, tradeMock ,null);
    }

    @Test
    public void WhenCallCalculateUnitsSizeForLondTradeWithCorrectSetting_CorrectResult(){
        setFakeTrade(Direction.UP, 1.2000, 1.1980);
        setFakeBrokerGateway(2000, 200, 1000 );
        setFalseInputForCalculatingUnitsSize(1.2000, 0.01);

        BigDecimal unitsSize = orderStrategy.calculateUnitsSize(brokerGatewayMock, priceMock, tradeMock, configurationMock);

        assertEquals(BigDecimal.valueOf(5988), unitsSize);
    }

    @Test
    public void WhenCallCalculateUnitsSizeForShortTradeWithCorrectSetting_CorrectResult(){
        setFakeTrade(Direction.DOWN, 1.2000, 1.1980);
        setFakeBrokerGateway(2000, 200, 1000 );
        setFalseInputForCalculatingUnitsSize(1.2000, 0.01);

        BigDecimal unitsSize = orderStrategy.calculateUnitsSize(brokerGatewayMock, priceMock, tradeMock, configurationMock);

        assertEquals(BigDecimal.valueOf(-5988), unitsSize);
    }

    @Test
    public void WhenCallCalculateUnitsSizeForTradeWithZeroBalance_CorrectResult(){
        setFakeTrade(Direction.FLAT, 1.2000, 1.1980);
        setFakeBrokerGateway(2000, 200, 0 );
        setFalseInputForCalculatingUnitsSize(1.2000, 0.01);

        BigDecimal unitsSize = orderStrategy.calculateUnitsSize(brokerGatewayMock, priceMock, tradeMock, configurationMock);

        assertEquals(BigDecimal.valueOf(0), unitsSize);
    }

    @Test
    public void WhenCallPlaceTradeAsOrderWithCorrectSettings_NewTransactionID(){
        String leverage = "30";
        String expectedID = "1122";
        String lastOrderTransactionID = (String) commonMembers.extractFieldObject(orderStrategy, "lastOrderTransactionID");
        setFakeTrade(Direction.UP, 1.2000, 1.1980);
        setFakeBrokerGateway(2000, 200, 1000 );
        setFalseInputForCalculatingUnitsSize(1.2000, 0.01);
        when(connectorMock.getLeverage()).thenReturn(leverage);
        when(brokerGatewayMock.placeMarketIfTouchedOrderOrder(any(HashMap.class))).thenReturn(expectedID);
        orderStrategy.placeTradeAsOrder(brokerGatewayMock, priceMock, tradeMock, configurationMock);
        String lastID = (String) commonMembers.extractFieldObject(orderStrategy, "lastOrderTransactionID");

        assertNotEquals(lastOrderTransactionID, lastID);
        assertEquals(expectedID, lastID);
    }

    private void setFakeBrokerGateway(double availableMargin, double marginUsed, double balance) {
        when(brokerGatewayMock.getConnector()).thenReturn(connectorMock);
        when(brokerGatewayMock.getAvailableMargin()).thenReturn(BigDecimal.valueOf(availableMargin));
        when(brokerGatewayMock.getMarginUsed()).thenReturn(BigDecimal.valueOf(marginUsed));
        when(brokerGatewayMock.getBalance()).thenReturn(BigDecimal.valueOf(balance));
    }


    private void setFalseInputForCalculatingUnitsSize(double currentPrice, double riskPerTrade) { ;
        when(priceMock.getBid()).thenReturn(BigDecimal.valueOf(currentPrice));
        when(configurationMock.getRiskPerTrade()).thenReturn(BigDecimal.valueOf(riskPerTrade));
    }

    private void setFakeTrade(Direction direction, double entryPrice, double stopLosPrice) {
        when(tradeMock.getEntryPrice()).thenReturn(BigDecimal.valueOf(entryPrice));
        when(tradeMock.getStopLossPrice()).thenReturn(BigDecimal.valueOf(stopLosPrice));
        when(tradeMock.getDirection()).thenReturn(direction);
    }


}
