package trader.strategy.bgxstrategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.connector.ApiConnector;
import trader.controller.Observer;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.indicator.Indicator;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.strategy.Observable;
import trader.strategy.bgxstrategy.configuration.BGXConfiguration;
import trader.strategy.observable.PriceObservable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BGXStrategyMainTest {

    private static final int CANDLESTICK_LIST_SIZE = 170;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);

    private ApiConnector apiConnectorMock;
    private BGXStrategyMain bgxStrategyMain;
    private BGXConfiguration bgxConfigurationMock;
    private List<HashMap<String, String>> falseIndicators;
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
//        timeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
//        candlesticks = new ArrayList<>();
//        applySettings();
//        fillCandlestickList();
        commonMembers = new CommonTestClassMembers();
        apiConnectorMock = mock(ApiConnector.class);
        bgxConfigurationMock = mock(BGXConfiguration.class);
        bgxStrategyMain = new BGXStrategyMain(apiConnectorMock);
        setFalseInitialIndicators();
    }


    @Test(expected = NullArgumentException.class)
    public void WhenApiConnectorIsNull_Exception(){
        new BGXStrategyMain(null);
    }

    @Test
    public void WhenCreateWithNullConfiguration_Exception(){
        BGXConfiguration configuration = bgxStrategyMain.getConfiguration();
        assertNotNull(configuration);
    }

    @Test
    public void WhenCreateConfigurationThenIndicatorsMustNotBeEmpty(){
        assertNotEquals(0, bgxStrategyMain.getConfiguration().getIndicators().size());
    }

    @Test
    public void addIndicatorsToPriceObservable_CorrectResult(){
        bgxStrategyMain.addIndicatorsFromConfiguration(falseIndicators);
        CopyOnWriteArrayList<Observer> observers = extractObservers();

        assertEquals(falseIndicators.size(), observers.size());
        assertEqualsFalseIndicatorsToObservers(observers);
    }






    private void assertEqualsFalseIndicatorsToObservers(CopyOnWriteArrayList<Observer> observers) {
        for (int i = 0; i <falseIndicators.size() ; i++) {
            HashMap<String, String> falseIndicator = falseIndicators.get(i);
            Indicator indicator = (Indicator) commonMembers.extractFieldObject(observers.get(i), "indicator");
            assertPeriod(falseIndicator, indicator);
            assertCandlePriceType(falseIndicator, indicator);
            assertGranularity(falseIndicator, indicator);
        }
    }

    @SuppressWarnings("unchecked")
    private CopyOnWriteArrayList<Observer> extractObservers() {
        PriceObservable priceObservable = (PriceObservable) commonMembers.extractFieldObject(bgxStrategyMain, "priceObservable");
        return (CopyOnWriteArrayList<Observer>) commonMembers.extractFieldObject(priceObservable, "observers");
    }

    private void assertGranularity(HashMap<String, String> falseIndicator, Indicator indicator) {
        CandleGranularity actual = (CandleGranularity) commonMembers.extractFieldObject(indicator, "granularity");
        String expected = falseIndicator.get("granularity");
        assertEquals(expected.toUpperCase(), actual.toString());
    }

    private void assertCandlePriceType(HashMap<String, String> falseIndicator, Indicator indicator) {
        CandlePriceType actual = (CandlePriceType) commonMembers.extractFieldObject(indicator, "candlePriceType");
        String expected = falseIndicator.get("candlePriceType");
        assertEquals(expected.toUpperCase(), actual.toString());
    }

    private void assertPeriod(HashMap<String, String> falseIndicator, Indicator indicator) {
        long actual = (long) commonMembers.extractFieldObject(indicator, "indicatorPeriod");
        String expected = falseIndicator.get("period");
        assertEquals(expected, String.valueOf(actual));
    }

    private void setFalseInitialIndicators() {
        falseIndicators = new ArrayList<>();
        falseIndicators.add(createFalseIndicator("rsi", "9", "Close", "m30"));
        falseIndicators.add(createFalseIndicator("sma", "16", "Open", "m15", "Simple"));
        falseIndicators.add(createFalseIndicator("wma", "22", "Low", "m10", "Weighted"));
        falseIndicators.add(createFalseIndicator("ema", "50", "High", "m5", "Exponential"));
        falseIndicators.add(createFalseIndicator("sma", "2", "Median", "m30", "SIMPLE"));
    }

    private HashMap<String, String> createFalseIndicator(String... args) {
        HashMap<String, String> indicator = new HashMap<>();
        indicator.put("type", args[0]);
        indicator.put("period", args[1]);
        indicator.put("candlePriceType", args[2]);
        indicator.put("granularity", args[3]);
        if(args[0].toLowerCase().trim().contains("ma")){
            indicator.put("maType", args[4]);
        }
        return indicator;
    }

//    private void addIndicatorB() {
//        String indicatorB = "emaIndicator";
//        HashMap<String, String> settingsB = indicatorASettings("9", "MEDIAN");
//        settingsB.put("maType", "EXPONENTIAL");
//        bgxStrategyMain.addIndicators(indicatorB, settingsB);
//    }
//
//    private void addIndicatorA() {
//        String indicatorA = "rsiIndicator";
//        HashMap<String, String> settingsA = indicatorASettings("17", "LOW");
//        bgxStrategyMain.addIndicators(indicatorA, settingsA);
//    }
//
//    private HashMap<String, String> indicatorASettings(String s, String low) {
//        HashMap<String, String> settingsA = new HashMap<>();
//        settingsA.put("period", s);
//        settingsA.put("candlePriceType", low);
//        return settingsA;
//    }

//
//    @Test(expected = SubmitNewOrderCalledException.class)
//    public void ifNoOpenOrdersAndNoOpenTrades_SubmitNewOrder(){
//        commonMembers.changeFieldObject(bgxStrategyMain, "orderService", mockOrderService);
//        when(apiConnector.getOpenOrders()).thenReturn(new ArrayList<>());
//        when(apiConnector.getOpenTrades()).thenReturn(new ArrayList<>());
//        bgxStrategyMain.execute();
//    }
//
//    @Test(expected = CloseUnfilledOrderCalledException.class)
//    public void ifNoOpenTradesButHaveOpenOrders_closeUnfilledOrder(){
//        commonMembers.changeFieldObject(bgxStrategyMain, "orderService", mockOrderService);
//        when(apiConnector.getOpenTrades()).thenReturn(new ArrayList<>());
//        bgxStrategyMain.execute();
//    }
//
//    @Test(expected = ExitStrategyExecuteCalledException.class)
//    public void ifOpenTrades_Execute(){
//        commonMembers.changeFieldObject(bgxStrategyMain, "exitStrategy", mockExitStrategy);
//        bgxStrategyMain.execute();
//    }
//
//    @Test
//    public void testToString(){
//       assertEquals("bgxstrategy", bgxStrategyMain.toString());
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