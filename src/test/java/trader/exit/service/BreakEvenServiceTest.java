package trader.exit.service;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BreakEvenServiceTest {

    private TradingStrategyConfiguration configurationMock;
    private BrokerGateway brokerGatewayMock;
    private Price priceMock;
    private BrokerTradeDetails tradeDetailsMock;
    private BreakEvenService service;

    @Before
    public void setUp() throws Exception {
        configurationMock = mock(TradingStrategyConfiguration.class);
        brokerGatewayMock = mock(BrokerGateway.class);
        priceMock = mock(Price.class);
        tradeDetailsMock = mock(BrokerTradeDetails.class);
        service = new BreakEvenService();
    }

    @Test
    public void givenLongTradeWithStopLossAboveTradeOpenPrice_WhenCallMoveToBreakEven_ThenNoStopLossMove() {
        setFakePrice(1.2407, 1.2405);
        setFakeBrokerTradeDetails(0, "12", null, 1.2325, 1.2325 ,100, 100  );

        service.moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock);

        verify(brokerGatewayMock, never()).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenLongTradeWithStopLossAboveBreakEvenPrice_WhenCallMoveToBreakEven_ThenNoStopLossMove() {
        setFakePrice(1.2337, 1.2335);
        setFakeBrokerTradeDetails(0, "12", null, 1.2325, 1.2315 ,100, 100  );
        service.moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock);

        verify(brokerGatewayMock, never()).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenLongTradeWithCorrectSettings_WhenCallMoveToBreakEven_ThenMoveStopLossToBreakEven() {
        String transactionID = "22";
        setFakePrice(1.2407, 1.2405);
        setFakeBrokerTradeDetails(0, "12", "14", 1.2345, 1.2315 ,100, 100  );
        when(brokerGatewayMock.setTradeStopLossPrice(anyString(), anyString())).thenReturn(transactionID);

        service.moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenShortTradeWithStopLossBelowTradeOpenPrice_WhenCallMoveToBreakEven_ThenNoStopLossMove() {
        setFakePrice(1.2307, 1.2305);
        setFakeBrokerTradeDetails(0, "12", "14", 1.2395, 1.2315 ,-100, -100  );

        service.moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock);

        verify(brokerGatewayMock, never()).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenShortTradeAndStopLossIsBelowBreakEven_WhenCallExecute_ThenNoStopLossMove(){
        setFakePrice(1.2377, 1.2375);
        setFakeBrokerTradeDetails(0, "12", "14", 1.2395, 1.2416 ,-100, -100  );

        service.moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock);

        verify(brokerGatewayMock, never()).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenShortTradeWithCorrectSettings_WhenCallMoveToBreakEven_ThenMoveStopLossToBreakEven() {
        String transactionID = "22";
        setFakePrice(1.2307, 1.2305);
        setFakeBrokerTradeDetails(0, "12", "14", 1.2395, 1.2415 ,-100, -100  );
        when(brokerGatewayMock.setTradeStopLossPrice(anyString(), anyString())).thenReturn(transactionID);

        service.moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
    }

    private void setFakeBrokerTradeDetails(int tradeIndex ,String tradeId, String orderID, double entryPrice, double stopLossPrice , double initialUnits  , double units){
        when(brokerGatewayMock.getTradeDetails(tradeIndex)).thenReturn(tradeDetailsMock);
        when(tradeDetailsMock.getTradeID()).thenReturn(tradeId);
        when(tradeDetailsMock.getStopLossOrderID()).thenReturn(orderID);
        when(tradeDetailsMock.getOpenPrice()).thenReturn(BigDecimal.valueOf(entryPrice));
        when(tradeDetailsMock.getStopLossPrice()).thenReturn(BigDecimal.valueOf(stopLossPrice));
        when(tradeDetailsMock.getCurrentUnits()).thenReturn(BigDecimal.valueOf( units));
        when(tradeDetailsMock.getInitialUnits()).thenReturn(BigDecimal.valueOf(initialUnits));
    }

    private void setFakePrice(double ask, double bid){
        when(priceMock.getAsk()).thenReturn(BigDecimal.valueOf(ask));
        when(priceMock.getBid()).thenReturn(BigDecimal.valueOf(bid));
    }

    @Test
    public void givenService_WhenCallToString_ThenReturnCorrectString(){
        assertEquals("position to break even", service.toString());
    }
}
