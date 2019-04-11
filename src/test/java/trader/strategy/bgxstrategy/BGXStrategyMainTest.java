package trader.strategy.bgxstrategy;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.controller.CreateTradeController;
import trader.entry.EntryStrategy;
import trader.entry.StandardEntryStrategy;
import trader.exception.EmptyArgumentException;
import trader.observer.Observer;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.configuration.TradingStrategyConfiguration;
import trader.strategy.Observable;
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
    private static final String BROKER_NAME = "Oanda";
    private static final String BGX_STRATEGY_CONFIG_FILE_NAME = "bgxStrategyConfig.yaml";
    private static final String BROKER_CONFIG_FILE_NAME = "oandaBrokerConfig.yaml";
    private static final String ENTRY_STRATEGY_NAME = "standard";
    private static final String ORDER_STRATEGY_NAME = "standard";

    private BGXStrategyMain bgxStrategyMain;
    private TradingStrategyConfiguration bgxConfigurationMock;
    private List<HashMap<String, String>> falseIndicators;
    private CommonTestClassMembers commonMembers;

//    private List<TradeImpl> trades;
//    private List<Order> orders;
//    private Order mockOrder;
//    private ApiConnector apiConnector;

//    private TradeImpl mockTrade;

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
//        mockTrade = mock(TradeImpl.class);
//        timeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
//        candlesticks = new ArrayList<>();
//        applySettings();
//        fillCandlestickList();
        commonMembers = new CommonTestClassMembers();
        bgxConfigurationMock = mock(TradingStrategyConfiguration.class);
        setFalseInitialIndicators();
        bgxStrategyMain = new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME, ENTRY_STRATEGY_NAME, ORDER_STRATEGY_NAME);

    }

    @Test(expected = NullArgumentException.class)
    public void WhenBrokerNameIsNull_Exception(){
    new BGXStrategyMain(null, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME, ENTRY_STRATEGY_NAME, ORDER_STRATEGY_NAME);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenStrategyConfigFileNameIsNull_Exception(){
        new BGXStrategyMain(BROKER_NAME, null, BROKER_CONFIG_FILE_NAME, ENTRY_STRATEGY_NAME, ORDER_STRATEGY_NAME);
    }
    @Test(expected = NullArgumentException.class)
    public void WhenBrokerConfigFileNameIsNull_Exception(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, null, ENTRY_STRATEGY_NAME, ORDER_STRATEGY_NAME);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenEntryStrategyNameIsNull_Exception(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME, null, ORDER_STRATEGY_NAME);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenOrderStrategyNameIsNull_Exception(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME, ENTRY_STRATEGY_NAME, null);
    }

    @Test
    public void WhenCreateWithNullConfiguration_Exception(){
        TradingStrategyConfiguration configuration = bgxStrategyMain.getConfiguration();
        assertNotNull(configuration);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenBrokerNameIsEmpty_Exception(){
        new BGXStrategyMain("  ", BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME, ENTRY_STRATEGY_NAME, ORDER_STRATEGY_NAME);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenStrategyConfigFileNameIsEmpty_Exception(){
        new BGXStrategyMain(BROKER_NAME, "  ", BROKER_CONFIG_FILE_NAME, ENTRY_STRATEGY_NAME, ORDER_STRATEGY_NAME);
    }
    @Test(expected = EmptyArgumentException.class)
    public void WhenBrokerConfigFileNameIsEmpty_Exception(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, "  ", ENTRY_STRATEGY_NAME, ORDER_STRATEGY_NAME);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenEntryStrategyNameIsEmpty_Exception(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME, "  ", ORDER_STRATEGY_NAME);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenOrderStrategyNameIsEmpty_Exception(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME, ENTRY_STRATEGY_NAME, "  ");
    }

    @Test
    public void WhenCreateWithNullBrokerConfiguration_Exception(){
        BrokerGateway brokerGateway = bgxStrategyMain.getBrokerGateway();
        assertNotNull(brokerGateway);
    }

    @Test
    public void WhenCreateConfigurationThenIndicatorsMustNotBeEmpty(){
        assertNotEquals(0, bgxStrategyMain.getConfiguration().getIndicators().size());
    }

    @Test
    public void WhenCallCreateIndicatorsFromConfiguration_CorrectResult(){
        List<Indicator> indicatorsFromConfiguration = bgxStrategyMain.createIndicatorsFromConfiguration(falseIndicators);

        assertEquals(falseIndicators.size(), indicatorsFromConfiguration.size());
        assertIndicatorsEquality(indicatorsFromConfiguration);
    }

    @Test
    public void addIndicatorsToPriceObservable_CorrectResult(){
        PriceObservable priceObservable = extractPriceObservable();
        extractObservers(priceObservable).clear();
        List<Indicator> indicatorsFromConfiguration = bgxStrategyMain.createIndicatorsFromConfiguration(falseIndicators);
        bgxStrategyMain.addIndicatorsToObservable(priceObservable, indicatorsFromConfiguration);
        CopyOnWriteArrayList<Observer> observers = extractObservers(priceObservable);

        assertEquals(falseIndicators.size(), observers.size());
        assertEqualsFalseIndicatorsToObservers(observers);
    }

    @Test
    public void WhenAddBrokerGatewayWithCorrectInputs_CorrectAdd(){
        BrokerGateway brokerGateway = (BrokerGateway) commonMembers.extractFieldObject(bgxStrategyMain, "brokerGateway");

        String actual = brokerGateway.getClass().getSimpleName();
        String expected = BROKER_NAME + "Gateway";

        assertEquals(expected, actual);
        assertNotNull(commonMembers.extractFieldObject(brokerGateway, "connector"));
        assertNotNull(commonMembers.extractFieldObject(brokerGateway, "context"));
    }

    @Test
    public void WhenInstantiateEntryStrategyFieldMustNotBeNull(){
        EntryStrategy entryStrategy = (EntryStrategy) commonMembers.extractFieldObject(bgxStrategyMain, "entryStrategy");
        Object createTradeController = commonMembers.extractFieldObject(entryStrategy, "createTradeController");
        Object rsi = commonMembers.extractFieldObject(entryStrategy, "rsi");

        assertNotNull(entryStrategy);
        assertNotNull(createTradeController);
        assertNotNull(rsi);
    }

    @Test
    public void WhenCallSetPositionObserverThenHeMustHaveEntryStrategy(){
        Observer observer = bgxStrategyMain.setPositionObserver();
        Object entryStrategy = commonMembers.extractFieldObject(observer, "entryStrategy");

        assertNotNull(entryStrategy);
        assertEquals(StandardEntryStrategy.class, entryStrategy.getClass());
    }

    @Test
    public void WhenInstantiateOrderStrategyItMustBeCorrectType(){
        EntryStrategy entryStrategy = (EntryStrategy) commonMembers.extractFieldObject(bgxStrategyMain, "entryStrategy");
        Object createTradeController = commonMembers.extractFieldObject(entryStrategy, "createTradeController");
        Object rsi = commonMembers.extractFieldObject(entryStrategy, "rsi");

        assertNotNull(entryStrategy);
        assertNotNull(createTradeController);
        assertNotNull(rsi);
    }

//    @Test
//    public void WhenCallSetPositionObserverThenHeMustHaveEntryStrategy(){
//        Observer observer = bgxStrategyMain.setPositionObserver();
//        Object entryStrategy = commonMembers.extractFieldObject(observer, "entryStrategy");
//
//        assertNotNull(entryStrategy);
//        assertEquals(StandardEntryStrategy.class, entryStrategy.getClass());
//    }



    private void assertEqualsFalseIndicatorsToObservers(CopyOnWriteArrayList<Observer> observers) {
        for (int i = 0; i <falseIndicators.size() ; i++) {
            HashMap<String, String> falseIndicator = falseIndicators.get(i);
            Indicator indicator = (Indicator) commonMembers.extractFieldObject(observers.get(i), "indicator");
            assertEqualsFalseIndicatorToReal(falseIndicator, indicator);
        }
    }

    private void assertIndicatorsEquality(List<Indicator> indicators) {
        for (int i = 0; i <falseIndicators.size() ; i++) {
            HashMap<String, String> falseIndicator = falseIndicators.get(i);
            assertEqualsFalseIndicatorToReal(falseIndicator, indicators.get(i));
        }
    }

    private void assertEqualsFalseIndicatorToReal(HashMap<String, String> falseIndicator, Indicator indicator) {
        assertPeriod(falseIndicator, indicator);
        assertCandlePriceType(falseIndicator, indicator);
        assertGranularity(falseIndicator, indicator);
    }

    @SuppressWarnings("unchecked")
    private CopyOnWriteArrayList<Observer> extractObservers(Observable observable) {
        return (CopyOnWriteArrayList<Observer>) commonMembers.extractFieldObject(observable, "observers");
    }

    private PriceObservable extractPriceObservable() {
        return (PriceObservable) commonMembers.extractFieldObject(bgxStrategyMain, "priceObservable");
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
        falseIndicators.add(createFalseIndicator("Simple", "16", "Open", "m15", "price"));
        falseIndicators.add(createFalseIndicator("Weighted", "22", "Low", "m10", "slow"));
        falseIndicators.add(createFalseIndicator("Weighted", "50", "High", "m5", "fast"));
        falseIndicators.add(createFalseIndicator("SIMPLE", "2", "Median", "m30", "daily"));
        falseIndicators.add(createFalseIndicator("Exponential", "17", "Low", "m10", "middle"));
    }

    private HashMap<String, String> createFalseIndicator(String... args) {
        HashMap<String, String> indicator = new HashMap<>();
        indicator.put("type", args[0]);
        indicator.put("period", args[1]);
        indicator.put("candlePriceType", args[2]);
        indicator.put("granularity", args[3]);
        if(!args[0].toLowerCase().trim().contains("rsi")){
            indicator.put("position", args[4]);
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