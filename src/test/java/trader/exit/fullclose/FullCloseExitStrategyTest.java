package trader.exit.fullclose;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.exit.BaseExitStrategyTest;
import trader.exit.service.BreakEvenService;
import trader.exit.service.ClosePositionService;
import trader.presenter.Presenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FullCloseExitStrategyTest extends BaseExitStrategyTest {

    private BreakEvenService breakEvenServiceMock;
    private ClosePositionService closePositionServiceMock;
    private FullCloseExitStrategy strategy;
    private Presenter presenterMock;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        breakEvenServiceMock = mock(BreakEvenService.class);
        closePositionServiceMock = mock(ClosePositionService.class);
        presenterMock = mock(Presenter.class);
        strategy = new FullCloseExitStrategy();
        strategy.setConfiguration(configurationMock);
        strategy.setBrokerGateway(brokerGatewayMock);
        strategy.setPresenter(presenterMock);
        setUpdateCandlesServiceToReturnFalse();

    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetConfigurationWithNull_Exception() {
        FullCloseExitStrategy fullCloseExitStrategy = new FullCloseExitStrategy();
        fullCloseExitStrategy.setConfiguration(null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetBrokerGatewayWithNull_Exception() {
        FullCloseExitStrategy fullCloseExitStrategy = new FullCloseExitStrategy();
        fullCloseExitStrategy.setBrokerGateway(null);
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
        assertEquals("Exit strategy: FULL CLOSE", strategy.toString());
    }

    @Test
    public void givenLongTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEven() {
        setClosePositionServiceToReturnFalse();
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
    public void givenShortTradeWithCorrectSettings_WhenCallExecute_ThenMoveStopToBreakEven() {
        setClosePositionServiceToReturnFalse();
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
    public void givenLongTradeAndCorrectPrice_WhenCallExecute_ThenCloseFullPosition(){
        setBreakEvenServiceToReturnFalse();
        setFakePrice("1.13120", "1.1314");
        setFakeBrokerGateway("1.1214");
        setFakeTradeDetails("11", "1.1254", "1.1234", "100", "100");
        setFakePresenter();

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).placeOrder(any(HashMap.class), anyString());
        verify(presenterMock, times(1)).execute(anyString(), anyString(), anyString());
    }

    @Test
    public void givenShortTradeAndCorrectPrice_WhenCallExecute_ThenCloseFullPosition(){
        setBreakEvenServiceToReturnFalse();
        setFakePrice("1.1194", "1.1196");
        setFakeBrokerGateway("1.1294");
        setFakeTradeDetails("11", "1.1254", "1.1274", "-100", "-100");
        setFakePresenter();

        strategy.execute(priceMock);

        verify(brokerGatewayMock, times(1)).placeOrder(any(HashMap.class), anyString());
        verify(presenterMock, times(1)).execute(anyString(), anyString(), anyString());

    }

    @Test(expected = NullArgumentException.class)
    public void givenNullPresenter_WhenCallExecute_ThenThrowException(){
        FullCloseExitStrategy fullCloseExitStrategy = new FullCloseExitStrategy();
        fullCloseExitStrategy.setPresenter(null);
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

    private void setFakePresenter(){
        doNothing().when(presenterMock).execute(anyString());
        commonMembers.changeFieldObject(strategy, "presenter", presenterMock);
    }
}
