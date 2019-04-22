package trader.exit.service;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.entity.candlestick.Candlestick;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TrailStopLossServiceTest {

    private BrokerTradeDetails tradeDetailsMock;
    private Candlestick candlestickMock;
    private BrokerGateway brokerGatewayMock;
    private TrailStopLossService service;

    @Before
    public void setUp() throws Exception {

        tradeDetailsMock = mock(BrokerTradeDetails.class);
        candlestickMock = mock(Candlestick.class);
        brokerGatewayMock = mock(BrokerGateway.class);
        service = new TrailStopLossService();
    }

    @Test
    public void givenEmptyExitBar_WhenCallSetExitBarComponents_ThenSetComponentsToTradeOpenPrice(){
        BigDecimal openPrice = BigDecimal.valueOf(1.1234);
        when(tradeDetailsMock.getOpenPrice()).thenReturn(openPrice);

        BigDecimal exitBarHighInitial = service.getExitBarHigh();
        BigDecimal exitBarLowInitial = service.getExitBarLow();
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);

        assertNull(exitBarHighInitial);
        assertNull(exitBarLowInitial);
        assertEquals(openPrice, service.getExitBarHigh());
        assertEquals(openPrice, service.getExitBarLow());
    }

    @Test
    public void givenLongTradeAndNonEmptyExitBarAndCandlestickBelowTradeOpenPrice_WhenCallSetExitBarComponents_ThenSetComponentsToProperPrice(){
        setFakeTradeDetails("100", "1.1234", "11");
        setFakeCandlestick("1.1230", "1.1231", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);

        assertEquals(new BigDecimal("1.1234"), service.getExitBarLow());
        assertEquals(new BigDecimal("1.1234"), service.getExitBarHigh());
    }

    @Test
    public void givenLongTradeAndNonEmptyExitBarAndCandlestickAboveTradeOpenPrice_WhenCallSetExitBarComponents_ThenSetComponentsToProperPrice(){
        setFakeTradeDetails("100", "1.1234", "11");
       setFakeCandlestick("1.1236", "1.1238", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);

        assertEquals(new BigDecimal("1.1236"), service.getExitBarLow());
        assertEquals(new BigDecimal("1.1238"), service.getExitBarHigh());
    }

    @Test
    public void givenShortTradeAndNonEmptyExitBarAndCandlestickAboveTradeOpenPrice_WhenCallSetExitBarComponents_ThenSetComponentsToProperPrice(){
        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeCandlestick("1.1241", "1.1247", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);

        assertEquals(new BigDecimal("1.1234"), service.getExitBarLow());
        assertEquals(new BigDecimal("1.1234"), service.getExitBarHigh());
    }

    @Test
    public void givenShortTradeAndNonEmptyExitBarAndCandlestickBelowTradeOpenPrice_WhenCallSetExitBarComponents_ThenSetComponentsToProperPrice(){
        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeCandlestick("1.1230", "1.1231", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);

        assertEquals(new BigDecimal("1.1230"), service.getExitBarLow());
        assertEquals(new BigDecimal("1.1231"), service.getExitBarHigh());
    }


    @Test
    public void givenLongTradeWithStopLossPriceAboveExitBarLow_WhenCallIsReadyToSendTrailOrder_ThenReturnFalse() {
        setFakeTradeDetails("100", "1.1234", "11");
        setFakeBrokerGateway("1.1321");
        setFakeCandlestick("1.1245", "1.1247", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToSendTrailOrder(brokerGatewayMock, tradeDetailsMock);

        assertFalse(readyToSendTrailOrder);
    }

    @Test
    public void givenLongTradeWithStopLossPriceBelowExitBarLow_WhenCallIsReadyToSendTrailOrder_ThenReturnTrue() {
        setFakeTradeDetails("122", "1.1237", "11");
        setFakeBrokerGateway("1.1221");
        setFakeCandlestick("1.1245", "1.1247", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToSendTrailOrder(brokerGatewayMock, tradeDetailsMock);

        assertTrue(readyToSendTrailOrder);
    }

    @Test
    public void givenLongTradeWithStopLossPriceZero_WhenCallIsReadyToSendTrailOrder_ThenReturnTrue() {
        setFakeTradeDetails("122", "1.1237", "11");
        setFakeBrokerGateway("0");
        setFakeCandlestick("1.1245", "1.1247", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToSendTrailOrder(brokerGatewayMock, tradeDetailsMock);

        assertTrue(readyToSendTrailOrder);
    }

    @Test
    public void givenShortTradeWithStopLossPriceBelowExitBarLow_WhenCallIsReadyToSendTrailOrder_ThenReturnFalse() {
        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeBrokerGateway("1.1221");
        setFakeCandlestick("1.1227", "1.1229", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToSendTrailOrder(brokerGatewayMock, tradeDetailsMock);

        assertFalse(readyToSendTrailOrder);
    }

    @Test
    public void givenShortTradeWithStopLossPriceAboveExitBarHigh_WhenCallIsReadyToSendTrailOrder_ThenReturnTrue() {
        setFakeTradeDetails("-122", "1.1237", "11");
        setFakeBrokerGateway("1.1242");
        setFakeCandlestick("1.1225", "1.1227", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToSendTrailOrder(brokerGatewayMock, tradeDetailsMock);

        assertTrue(readyToSendTrailOrder);
    }

    @Test
    public void givenShortTradeWithStopLossPriceZero_WhenCallIsReadyToSendTrailOrder_ThenReturnTrue() {
        setFakeTradeDetails("-122", "1.1237", "11");
        setFakeBrokerGateway("0");
        setFakeCandlestick("1.1225", "1.1227", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToSendTrailOrder(brokerGatewayMock, tradeDetailsMock);

        assertTrue(readyToSendTrailOrder);
    }

    @Test
    public void givenLongTradeWithCandlestickBelowExitBar_WhenCallIsReadyToTrailStopLoss_ThenReturnFalse(){
        setFakeTradeDetails("100", "1.1234", "11");
        setFakeCandlestick("1.1122", "1.1123", "1.1229");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToTrailStopLoss(tradeDetailsMock, candlestickMock);

        assertFalse(readyToSendTrailOrder);
    }

    @Test
    public void givenLongTradeWithCandlestickAboveExitBar_WhenCallIsReadyToTrailStopLoss_ThenReturnTrue(){
        setFakeTradeDetails("100", "1.1234", "11");
        setFakeCandlestick("1.1345", "1.1347", "1.1346");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToTrailStopLoss(tradeDetailsMock, candlestickMock);

        assertTrue(readyToSendTrailOrder);
    }

    @Test
    public void givenShortTradeWithCandlestickAboveExitBar_WhenCallIsReadyToTrailStopLoss_ThenReturnFalse(){
        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeCandlestick("1.1333", "1.1356", "1.1341");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToTrailStopLoss(tradeDetailsMock, candlestickMock);

        assertFalse(readyToSendTrailOrder);
    }

    @Test
    public void givenShortTradeWithCandlestickBelowExitBar_WhenCallIsReadyToTrailStopLoss_ThenReturnTrue(){
        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeCandlestick("1.1125", "1.1127", "1.1126");

        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        boolean readyToSendTrailOrder = service.isReadyToTrailStopLoss(tradeDetailsMock, candlestickMock);

        assertTrue(readyToSendTrailOrder);
    }

    @Test
    public void givenTrailableSettings_WhenCallTrailStopLoss_ThenSetNewTailedStopLossPrice() {
        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeBrokerGateway("0");
        setFakeCandlestick("1.1125", "1.1127", "1.1126");

        service.trailStopLoss(tradeDetailsMock, candlestickMock, brokerGatewayMock);

        verify(brokerGatewayMock, times(1)).setTradeStopLossPrice(anyString(), anyString());
    }

    @Test
    public void givenNonTrailableSettings_WhenCallTrailStopLoss_ThenNoNewStopLossPrice() {
        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeBrokerGateway("1.1111");
        setFakeCandlestick("1.1125", "1.1127", "1.1126");

        service.trailStopLoss(tradeDetailsMock, candlestickMock, brokerGatewayMock);

        verify(brokerGatewayMock, times(0)).setTradeStopLossPrice(anyString(), anyString());
    }


    @Test
    public void givenLongTrade_WhenCallGetNewStopLossPrice_ThenReturnExitBarLow(){

        setFakeTradeDetails("100", "1.1234", "11");
        setFakeCandlestick("1.1324", "1.1327", "1.1326");
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);

        BigDecimal newStopLossPrice = service.getNewStopLossPrice(tradeDetailsMock);

        assertEquals(new BigDecimal("1.1324"), newStopLossPrice);
    }

    @Test
    public void givenShortTrade_WhenCallGetNewStopLossPrice_ThenReturnExitBarHigh(){

        setFakeTradeDetails("-100", "1.1234", "11");
        setFakeCandlestick("1.1124", "1.1127", "1.1126");
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);
        service.setExitBarComponents(tradeDetailsMock, candlestickMock);

        BigDecimal newStopLossPrice = service.getNewStopLossPrice(tradeDetailsMock);

        assertEquals(new BigDecimal("1.1127"), newStopLossPrice);
    }

    @Test
    public void givenService_WhenCallToString_ThenReturnCorrectString(){
        assertEquals("Stop loss trailed @ null", service.toString());
    }

    private void setFakeBrokerGateway(String stopLossPrice) {
        when(brokerGatewayMock.getTradeStopLossPrice(anyString())).thenReturn(new BigDecimal(stopLossPrice));
        when(brokerGatewayMock.setTradeStopLossPrice(anyString(), anyString())).thenReturn("1");
    }

    private void setFakeTradeDetails(String units, String openPrice, String id){
        when(tradeDetailsMock.getTradeID()).thenReturn(id);
        when(tradeDetailsMock.getCurrentUnits()).thenReturn(new BigDecimal( units));
        when(tradeDetailsMock.getOpenPrice()).thenReturn(new BigDecimal(openPrice));
    }

    private void setFakeCandlestick(String lowPrice, String highPrice, String closePrice){
        when(candlestickMock.getLowPrice()).thenReturn(new BigDecimal( lowPrice));
        when(candlestickMock.getHighPrice()).thenReturn(new BigDecimal(highPrice));
        when(candlestickMock.getClosePrice()).thenReturn(new BigDecimal(closePrice));
    }

}
