package trader.exit.halfclosetrail;

import org.junit.Before;
import org.junit.Test;
import trader.entity.candlestick.Candlestick;
import trader.exception.NullArgumentException;
import trader.exit.BaseExitStrategyTest;
import trader.exit.service.BreakEvenService;
import trader.exit.service.ClosePositionService;
import trader.exit.service.TrailStopLossService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class HalfCloseTrailExitStrategyTest extends BaseExitStrategyTest {

    private BreakEvenService breakEvenServiceMock;
    private ClosePositionService closePositionServiceMock;
    private TrailStopLossService trailStopLossServiceMock;
    private HalfCloseTrailExitStrategy strategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        breakEvenServiceMock = mock(BreakEvenService.class);
        closePositionServiceMock = mock(ClosePositionService.class);
        trailStopLossServiceMock = mock(TrailStopLossService.class);
        strategy = new HalfCloseTrailExitStrategy();
        strategy.setConfiguration(configurationMock);
        strategy.setBrokerGateway(brokerGatewayMock);
        strategy.setPresenter(presenterMock);
        setUpdateCandlesServiceToReturnFalse();

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

    @Test(expected = NullArgumentException.class)
    public void givenNullPresenter_WhenCallExecute_ThenThrowException(){
        HalfCloseTrailExitStrategy halfCloseTrailExitStrategy = new HalfCloseTrailExitStrategy();
        halfCloseTrailExitStrategy.setPresenter(null);
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

        setClosePositionServiceToReturnFalse();
        setTrailStopLossServiceToReturnFalse();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1280", "1.1282");
        setFakeBrokerGateway("1.1214");
        setFakeTradeDetails("11", "1.1254", "1.1234", "100", "100");
        setFakePresenter();

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(presenterMock, times(1)).execute(anyString());
    }

    @Test
    public void givenLongTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEvenAndCloseHalfPosition() {

        setTrailStopLossServiceToReturnFalse();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1290", "1.1290");
        setFakeBrokerGateway("1.1214");
        setFakeTradeDetails("11", "1.1254", "1.1234", "100", "100");
        setFakePresenter();


        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(brokerGatewayMock, times(1)).placeOrder(any(HashMap.class), anyString());
        verify(presenterMock, times(1)).execute(anyString());
        verify(presenterMock, times(1)).execute(anyString(), anyString(), anyString());
    }

    @Test
    public void givenLongTradeWithCorrectSettings_WhenCallExecute_ThenTrailStopLoss() {
        setBreakEvenServiceToReturnFalse();
        List<Candlestick> candlesticks = setFakeCandlesticksList("1.1289", "1.1286", "1.1287");
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(candlesticks);
        setFakePrice("1.1290", "1.1292");
        setFakeBrokerGateway("1.1214");
        setFakeTradeDetails("11", "1.1254", "1.1234", "100", "50");
        setFakePresenter();

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(presenterMock, times(1)).execute(anyString());
    }

    @Test
    public void givenShortTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEven() {

        setClosePositionServiceToReturnFalse();
        setTrailStopLossServiceToReturnFalse();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1226", "1.1228");
        setFakeBrokerGateway("1.1294");
        setFakeTradeDetails("11", "1.1254", "1.1274", "-100", "-100");
        setFakePresenter();

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(presenterMock, times(1)).execute(anyString());
    }

    @Test
    public void givenShortTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEvenAndCloseHalfPosition() {
        setTrailStopLossServiceToReturnFalse();
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(new ArrayList<>());
        setFakePrice("1.1216","1.1218");
        setFakeBrokerGateway("1.1294");
        setFakeTradeDetails("11", "1.1254", "1.1274", "-100", "-100");
        setFakePresenter();

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(brokerGatewayMock, times(1)).placeOrder(any(HashMap.class), anyString());
        verify(presenterMock, times(1)).execute(anyString());
        verify(presenterMock, times(1)).execute(anyString(), anyString(), anyString());
    }

    @Test
    public void givenShortTradeWithCorrectSettings_WhenCallExecute_ThenTrailStopLoss() {
        setBreakEvenServiceToReturnFalse();
        List<Candlestick> candlesticks = setFakeCandlesticksList("1.1219", "1.1216", "1.1217");
        when(updateCandlesServiceMock.getCandlesticks()).thenReturn(candlesticks);
        setFakePrice("1.1216", "1.1218");
        setFakeBrokerGateway("1.1294");
        setFakeTradeDetails("11", "1.1254", "1.1274", "-100", "-50");
        setFakePresenter();

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
        verify(presenterMock, times(1)).execute(anyString());

    }

    private void setUpdateCandlesServiceToReturnFalse() {
        when(updateCandlesServiceMock.updateCandles(brokerGatewayMock, configurationMock)).thenReturn(false);
        commonMembers.changeFieldObject(strategy, "updateCandlesService", updateCandlesServiceMock);
    }

    private void setBreakEvenServiceToReturnFalse() {
        when(breakEvenServiceMock.moveToBreakEven(tradeDetailsMock, priceMock, brokerGatewayMock)).thenReturn(false);
        commonMembers.changeFieldObject(strategy, "breakEvenService", breakEvenServiceMock);
    }

    private void setClosePositionServiceToReturnFalse() {
        when(closePositionServiceMock.closePosition(eq(tradeDetailsMock), eq(brokerGatewayMock), eq(configurationMock), any(BigDecimal.class))).thenReturn(false);
        commonMembers.changeFieldObject(strategy, "closePositionService", closePositionServiceMock);
    }

    private void setTrailStopLossServiceToReturnFalse() {
        when(trailStopLossServiceMock.trailStopLoss(eq(tradeDetailsMock), any(Candlestick.class), eq(brokerGatewayMock))).thenReturn(false);
        commonMembers.changeFieldObject(strategy, "trailStopLossService", trailStopLossServiceMock);
    }

    private void setFakePresenter(){
        doNothing().when(presenterMock).execute(anyString());
        commonMembers.changeFieldObject(strategy, "presenter", presenterMock);
    }

}