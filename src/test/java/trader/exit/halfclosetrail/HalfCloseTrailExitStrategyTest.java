package trader.exit.halfclosetrail;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;
import trader.exception.NullArgumentException;
import trader.exit.service.BreakEvenService;
import trader.exit.service.ClosePositionService;
import trader.exit.service.TrailStopLossService;
import trader.exit.service.UpdateCandlesService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class HalfCloseTrailExitStrategyTest {

    private Price priceMock;
    private UpdateCandlesService updateCandlesServiceMock;
    private BreakEvenService breakEvenServiceMock;
    private ClosePositionService closePositionServiceMock;
    private TrailStopLossService trailStopLossServiceMock;
    private BrokerGateway brokerGatewayMock;
    private TradingStrategyConfiguration configurationMock;
    private BrokerTradeDetails tradeDetailsMock;
    private HalfCloseTrailExitStrategy strategy;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {

        updateCandlesServiceMock = mock(UpdateCandlesService.class);
        breakEvenServiceMock = mock(BreakEvenService.class);
        closePositionServiceMock = mock(ClosePositionService.class);
        trailStopLossServiceMock = mock(TrailStopLossService.class);
        priceMock = mock(Price.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        brokerGatewayMock = mock(BrokerGateway.class);
        tradeDetailsMock = mock(BrokerTradeDetails.class);
        strategy = new HalfCloseTrailExitStrategy();
        strategy.setConfiguration(configurationMock);
        strategy.setBrokerGateway(brokerGatewayMock);
        commonMembers = new CommonTestClassMembers();
        setUpdateCandlesServiceMock();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetConfigurationWithNull_Exception() {
        HalfCloseTrailExitStrategy halfCloseTrailExitStrategy = new HalfCloseTrailExitStrategy();
        halfCloseTrailExitStrategy.setConfiguration(null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetBrokerGatewayWithNull_Exception() {
        HalfCloseTrailExitStrategy halfCloseTrailExitStrategy = new HalfCloseTrailExitStrategy();
        halfCloseTrailExitStrategy.setBrokerGateway(null);
    }

    @Test
    public void WhenCallSetConfigurationWithCorrectValue_CorrectUpdate() {
        Object configuration = commonMembers.extractFieldObject(strategy, "configuration");

        assertEquals(configurationMock.getClass(), configuration.getClass());
    }

    @Test
    public void WhenCallSetBrokerGatewayWithCorrectValue_CorrectUpdate() {
        Object gateway = commonMembers.extractFieldObject(strategy, "brokerGateway");

        assertEquals(brokerGatewayMock.getClass(), gateway.getClass());
    }

    @Test
    public void givenCorrectSettings_WhenCallToString_ThenReturnCorrectString() {
        assertEquals("Exit strategy: HALF CLOSE, TRAIL", strategy.toString());
    }


    @Test
    public void givenLongTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEven() {

        setClosePositionServiceMock();
        setTrailStopLossServiceMock();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1280", "1.1282");
        setFakeBrokerGateway("1.1214");
        setFakeTradeDetails("11", "1.1254", "1.1234", "100", "100");

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenLongTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEvenAndCloseHalfPosition() {

        setTrailStopLossServiceMock();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1290", "1.1290");
        setFakeBrokerGateway("1.1214");
        setFakeTradeDetails("11", "1.1254", "1.1234", "100", "100");


        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(brokerGatewayMock, times(1)).placeMarketOrder(any(HashMap.class));
    }

    @Test
    public void givenLongTradeWithCorrectSettings_WhenCallExecute_ThenTrailStopLoss() {
        setBreakEvenServiceMock();
        List<Candlestick> candlesticks = setFakeCandlesticksList("1.1289", "1.1286", "1.1287");
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(candlesticks);
        setFakePrice("1.1290", "1.1292");
        setFakeBrokerGateway("1.1214");
        setFakeTradeDetails("11", "1.1254", "1.1234", "100", "50");

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
    }



    @Test
    public void givenShortTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEven() {

        setClosePositionServiceMock();
        setTrailStopLossServiceMock();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1226", "1.1228");
        setFakeBrokerGateway("1.1294");
        setFakeTradeDetails("11", "1.1254", "1.1274", "-100", "-100");

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenShortTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEvenAndCloseHalfPosition() {

        setTrailStopLossServiceMock();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1216","1.1218");
        setFakeBrokerGateway("1.1294");
        setFakeTradeDetails("11", "1.1254", "1.1274", "-100", "-100");


        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(brokerGatewayMock, times(1)).placeMarketOrder(any(HashMap.class));
    }

    @Test
    public void givenShortTradeWithCorrectSettings_WhenCallExecute_ThenTrailStopLoss() {
        setBreakEvenServiceMock();
        List<Candlestick> candlesticks = setFakeCandlesticksList("1.1219", "1.1216", "1.1217");
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(candlesticks);
        setFakePrice("1.1216", "1.1218");
        setFakeBrokerGateway("1.1294");
        setFakeTradeDetails("11", "1.1254", "1.1274", "-100", "-50");

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
    }


    private List<Candlestick> setFakeCandlesticksList(String candleHighPrice, String candleLowPrice, String candleClosePrice) {
        Candlestick candlestickMock = mock(Candlestick.class);
        when(candlestickMock.getHighPrice()).thenReturn(new BigDecimal(candleHighPrice));
        when(candlestickMock.getLowPrice()).thenReturn(new BigDecimal(candleLowPrice));
        when(candlestickMock.getClosePrice()).thenReturn(new BigDecimal(candleClosePrice));
        List<Candlestick> candlesticks = new ArrayList<>();
        candlesticks.add(candlestickMock);
        candlesticks.add(candlestickMock);
        return candlesticks;
    }

    private void setFakePrice(String bid, String ask) {

        when(priceMock.getBid()).thenReturn(new BigDecimal(bid));
        when(priceMock.getAsk()).thenReturn(new BigDecimal(ask));
    }

    private void setFakeBrokerGateway(String tradeStopLossPrice) {
        when(brokerGatewayMock.getTradeDetails(anyInt())).thenReturn(tradeDetailsMock);
        when(brokerGatewayMock.getTradeStopLossPrice(anyString())).thenReturn(new BigDecimal(tradeStopLossPrice));
    }

    private void setFakeTradeDetails(String tradeID, String openPrice, String stopLossPrice, String initialUnits, String currentUnits) {
        when(tradeDetailsMock.getTradeID()).thenReturn(tradeID);
        when(tradeDetailsMock.getOpenPrice()).thenReturn(new BigDecimal(openPrice));
        when(tradeDetailsMock.getStopLossPrice()).thenReturn(new BigDecimal(stopLossPrice));
        when(tradeDetailsMock.getInitialUnits()).thenReturn(new BigDecimal(initialUnits));
        when(tradeDetailsMock.getCurrentUnits()).thenReturn(new BigDecimal(currentUnits));


    }

    private void setUpdateCandlesServiceMock() {
        doNothing().when(updateCandlesServiceMock).updateCandles(brokerGatewayMock, configurationMock);
        commonMembers.changeFieldObject(strategy, "updateCandlesService", updateCandlesServiceMock);
    }

    private void setBreakEvenServiceMock() {
        doNothing().when(breakEvenServiceMock).moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock);
        commonMembers.changeFieldObject(strategy, "breakEvenService", breakEvenServiceMock);
    }

    private void setClosePositionServiceMock() {
        doNothing().when(closePositionServiceMock).closePosition(eq(tradeDetailsMock), eq(brokerGatewayMock), eq(configurationMock), any(BigDecimal.class));
        commonMembers.changeFieldObject(strategy, "closePositionService", closePositionServiceMock);
    }


    private void setTrailStopLossServiceMock() {
        doNothing().when(trailStopLossServiceMock).trailStopLoss(eq(tradeDetailsMock), any(Candlestick.class), eq(brokerGatewayMock));
        commonMembers.changeFieldObject(strategy, "trailStopLossService", trailStopLossServiceMock);
    }

    private void setFakeConfigurations(String instrument, long initialCandleQuantity, CandleGranularity granularity, long updateQuantity) {
        when(configurationMock.getInstrument()).thenReturn(instrument);
        when(configurationMock.getInitialCandlesQuantity()).thenReturn(initialCandleQuantity);
        when(configurationMock.getExitGranularity()).thenReturn(granularity);
        when(configurationMock.getUpdateCandlesQuantity()).thenReturn(updateQuantity);
    }
}