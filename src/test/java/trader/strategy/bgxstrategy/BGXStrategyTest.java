package trader.strategy.bgxstrategy;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.connector.ApiConnector;
import trader.controller.Observer;
import trader.exception.NullArgumentException;
import trader.strategy.Observable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BGXStrategyTest {

    private static final int CANDLESTICK_LIST_SIZE = 170;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);

    private ApiConnector apiConnectorMock;
    private BGXStrategy bgxStrategy;
    private CommonTestClassMembers commonMembers;

//    private List<Trade> trades;
//    private List<Order> orders;
//    private Order mockOrder;
//    private ApiConnector apiConnector;

//    private Trade mockTrade;

//    private OrderService mockOrderService;
//    private ExitStrategy mockExitStrategy;
//    private ZonedDateTime timeNow;
//    private List<Candlestick> candlesticks;
//    private Candlestick mockCandle;

    @Before
    public void before() {
//        mockCandle = mock(Candlestick.class);
//        trades = new ArrayList<>();
//        orders = new ArrayList<>();
//        mockOrderService = mock(OrderService.class);
//        mockExitStrategy = mock(ExitStrategy.class);

//        mockOrder = mock(Order.class);
//        mockTrade = mock(Trade.class);
        commonMembers = new CommonTestClassMembers();
//        timeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
//        candlesticks = new ArrayList<>();
//        applySettings();
//        fillCandlestickList();
        commonMembers = new CommonTestClassMembers();
        apiConnectorMock = mock(ApiConnector.class);
        bgxStrategy = new BGXStrategy(apiConnectorMock);
    }


    @Test(expected = NullArgumentException.class)
    public void WhenApiConnectorIsNull_Exception(){
        new BGXStrategy(null);
    }

    @Test
    public void addIndicatorsToPriceObservable_CorrectResult(){
        addIndicatorA();
        addIndicatorB();

        Observable priceObservable = (Observable) commonMembers.extractFieldObject(bgxStrategy, "priceObservable");
        CopyOnWriteArrayList<Observer> observers = (CopyOnWriteArrayList<Observer>) commonMembers.extractFieldObject(priceObservable, "observers");

        assertEquals(2, observers.size());
    }

    private void addIndicatorB() {
        String indicatorB = "emaIndicator";
        HashMap<String, String> settingsB = indicatorASettings("9", "MEDIAN");
        settingsB.put("maType", "EXPONENTIAL");
        bgxStrategy.addIndicators(indicatorB, settingsB);
    }

    private void addIndicatorA() {
        String indicatorA = "rsiIndicator";
        HashMap<String, String> settingsA = indicatorASettings("17", "LOW");
        bgxStrategy.addIndicators(indicatorA, settingsA);
    }

    private HashMap<String, String> indicatorASettings(String s, String low) {
        HashMap<String, String> settingsA = new HashMap<>();
        settingsA.put("period", s);
        settingsA.put("candlePriceType", low);
        return settingsA;
    }

//
//    @Test(expected = SubmitNewOrderCalledException.class)
//    public void ifNoOpenOrdersAndNoOpenTrades_SubmitNewOrder(){
//        commonMembers.changeFieldObject(bgxStrategy, "orderService", mockOrderService);
//        when(apiConnector.getOpenOrders()).thenReturn(new ArrayList<>());
//        when(apiConnector.getOpenTrades()).thenReturn(new ArrayList<>());
//        bgxStrategy.execute();
//    }
//
//    @Test(expected = CloseUnfilledOrderCalledException.class)
//    public void ifNoOpenTradesButHaveOpenOrders_closeUnfilledOrder(){
//        commonMembers.changeFieldObject(bgxStrategy, "orderService", mockOrderService);
//        when(apiConnector.getOpenTrades()).thenReturn(new ArrayList<>());
//        bgxStrategy.execute();
//    }
//
//    @Test(expected = ExitStrategyExecuteCalledException.class)
//    public void ifOpenTrades_Execute(){
//        commonMembers.changeFieldObject(bgxStrategy, "exitStrategy", mockExitStrategy);
//        bgxStrategy.execute();
//    }
//
//    @Test
//    public void testToString(){
//       assertEquals("bgxstrategy", bgxStrategy.toString());
//    }
//
//
//
//    private void applySettings() {
//        trades.add(mockTrade);
//        orders.add(mockOrder);
//        orderSettings();
//        tradeSettings();
//        when(apiConnector.getInitialCandles()).thenReturn(candlesticks);
//        when(apiConnector.updateCandle()).thenReturn(mockCandle);
//    }
//
//    private void tradeSettings() {
//        when(apiConnector.getOpenTrades()).thenReturn(trades);
//        doThrow(new ExitStrategyExecuteCalledException()).when(mockExitStrategy).execute();
//    }
//
//    private void orderSettings() {
//        when(apiConnector.getOpenOrders()).thenReturn(orders);
//        doThrow(new SubmitNewOrderCalledException()).when(mockOrderService).submitNewOrder();
//        doThrow(new CloseUnfilledOrderCalledException()).when(mockOrderService).closeUnfilledOrder();
//    }
//
//    private void fillCandlestickList(){
//        for (int i = 0; i < CANDLESTICK_LIST_SIZE; i++) {
//            timeNow = timeNow.plusSeconds(30);
//            candlesticks.add(createCandlestickMock());
//            setMockCandleTime(timeNow = timeNow.plusSeconds(30));
//        }
//    }
//
//    private Candlestick createCandlestickMock() {
//        Candlestick newCandlestick = mock(Candlestick.class);
//        when(newCandlestick.getDateTime()).thenReturn(timeNow);
//        when(newCandlestick.getOpenPrice()).thenReturn(DEFAULT_PRICE);
//        when(newCandlestick.getHighPrice()).thenReturn(DEFAULT_PRICE);
//        when(newCandlestick.getLowPrice()).thenReturn(DEFAULT_PRICE);
//        when(newCandlestick.getClosePrice()).thenReturn(DEFAULT_PRICE);
//        return newCandlestick;
//    }
//
//    private void setMockCandleTime(ZonedDateTime time) {
//        when(mockCandle.getDateTime()).thenReturn(time);
//        when(mockCandle.isComplete()).thenReturn(true);
//    }
//
//    private class CloseUnfilledOrderCalledException extends RuntimeException{};
//    private class SubmitNewOrderCalledException extends RuntimeException{};
//    private class ExitStrategyExecuteCalledException extends RuntimeException{};

}