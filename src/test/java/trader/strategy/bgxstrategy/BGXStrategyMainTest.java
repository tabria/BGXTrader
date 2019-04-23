package trader.strategy.bgxstrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.broker.connector.BaseConnector;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.configuration.BGXConfigurationImpl;
import trader.entry.EntryStrategy;
import trader.entry.standard.StandardEntryStrategy;
import trader.exception.EmptyArgumentException;
import trader.exit.ExitStrategy;
import trader.observer.Observer;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.configuration.TradingStrategyConfiguration;
import trader.order.OrderStrategy;
import trader.order.standard.StandardOrderStrategy;
import trader.presenter.Presenter;
import trader.requestor.*;
import trader.responder.Response;
import trader.strategy.Observable;
import trader.strategy.observable.PriceObservable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestBuilderCreator.class, BaseGateway.class})
public class BGXStrategyMainTest {



    private static final int CANDLESTICK_LIST_SIZE = 170;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final String BROKER_NAME = "Oanda";
    private static final String BGX_STRATEGY_CONFIG_FILE_NAME = "bgxStrategyConfig.yaml";
    private static final String BROKER_CONFIG_FILE_NAME = "oandaBrokerConfig.yaml";
    private static final String ENTRY_STRATEGY_NAME = "standard";
    private static final String ORDER_STRATEGY_NAME = "standard";

    private RequestBuilder requestBuilderMock;
    private Request requestMock;
    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
  //  private UseCase useCaseSpy;
    private Response responseMock = mock(Response.class);
    private BrokerGateway brokerGatewayMock;
    private EntryStrategy entryStrategyMock;
    private OrderStrategy orderStrategyMock;
    private ExitStrategy exitStrategyMock;
    private TradingStrategyConfiguration configurationMock;
    private Presenter presenterMock;
    private BGXStrategyMain bgxStrategyMain;

    private List<Map<String, String>> falseIndicators;
    private CommonTestClassMembers commonMembers;

    @Before
    public void before() {
        commonMembers = new CommonTestClassMembers();
        brokerGatewayMock = mock(BrokerGateway.class);
        entryStrategyMock = mock(EntryStrategy.class);
        orderStrategyMock = mock(OrderStrategy.class);
        exitStrategyMock = mock(ExitStrategy.class);
        presenterMock = mock(Presenter.class);
        requestBuilderMock = mock(RequestBuilder.class);
        requestMock = mock(Request.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
 //       useCaseSpy = spy(UseCase.class);
        useCaseMock = mock(UseCase.class);
        responseMock = mock(Response.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
//        setFakeRequest();
//        setFakeResponse();
//        setFakeConfiguration();
//        setFalseInitialIndicators();
//        setFakeUseCaseFactory();
//        setFakeUseCase();
        bgxStrategyMain = new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME);

    }

    @Test(expected = NullArgumentException.class)
    public void givenNullBrokerName_WhenInitialize_ThenException(){
        new BGXStrategyMain(null, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME);
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullStrategyConfigFileName_WhenInitialize_ThenException (){
        new BGXStrategyMain(BROKER_NAME, null, BROKER_CONFIG_FILE_NAME);
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullBrokerConfigFileName_WhenInitialize_ThenException(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyBrokerName_WhenInitialize_ThenException(){
        new BGXStrategyMain("  ", BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyStrategyConfigFileName_WhenInitialize_ThenException(){
        new BGXStrategyMain(BROKER_NAME, "  ", BROKER_CONFIG_FILE_NAME);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyBrokerConfigFileName_WhenInitialize_ThenException(){
        new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, "  ");
    }

    @Test
    public void givenCorrectSettings_WhenInstantiate_ThenFieldsMustHaveValue(){
        when(requestMock.getBody()).thenReturn(setFakeConnectorSettings());
        BaseGateway gateway = mock(BaseGateway.class);
        PowerMockito.mockStatic(BaseGateway.class);
        PowerMockito.when(BaseGateway.create(anyString(), any(BrokerConnector.class), any(Presenter.class))).thenReturn(gateway);

        bgxStrategyMain = new BGXStrategyMain(BROKER_NAME, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME);

        Object configuration = commonMembers.extractFieldObject(bgxStrategyMain, "configuration");
        Object brokerGateway = commonMembers.extractFieldObject(bgxStrategyMain, "brokerGateway");

        assertNotNull(brokerGateway);
        assertNotNull(configuration);
        assertEquals(gateway, brokerGateway);
    }



    private void setFakeUseCaseFactory(){
        when(useCaseFactoryMock.make(anyString(), any(Presenter.class))).thenReturn(useCaseMock);
    }

    private void setFakeUseCase(){
        when(spy(useCaseMock).execute(any(Request.class))).thenReturn(responseMock);
       // doNothing().when(useCaseSpy).execute(any(Request.class));
    }

    private void setFakeRequest(){
        PowerMockito.mockStatic(RequestBuilderCreator.class);
        PowerMockito.when(RequestBuilderCreator.create(any())).thenReturn(requestBuilderMock);
        when(requestBuilderMock.build(any(HashMap.class))).thenReturn(requestMock);
        when(requestMock.getBody()).thenReturn(new HashMap<>());
    }

    private void setFakeResponse(){
        //when(useCaseSpy.execute(requestMock)).thenReturn(responseMock);
        when(useCaseFactoryMock.make(anyString(), any(Presenter.class))).thenReturn(useCaseMock);
    }

    private void setFakeConfiguration(){
        when(responseMock.getBody()).thenReturn(configurationMock);
    }

    private Map<String, Object> setFakeConnectorSettings() {
        Map<String, Object> wrapper = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("brokerName", "Oanda");
        settings.put("url", "http://sss.com");
        settings.put("token", "ssae1234redsad");
        settings.put("id", "12");
        settings.put("leverage", "1");
        wrapper.put("settings", settings);
        return wrapper;
    }

//    @Test
//    public void WhenCallCreateIndicatorsFromConfiguration_CorrectResult(){
//        List<Indicator> indicatorsFromConfiguration = bgxStrategyMain.createIndicatorsFromConfiguration(falseIndicators);
//
//        assertEquals(falseIndicators.size(), indicatorsFromConfiguration.size());
//        assertIndicatorsEquality(indicatorsFromConfiguration);
//    }

    @Test
    public void addIndicatorsToPriceObservable_CorrectResult(){
//        PriceObservable priceObservable = extractPriceObservable();
//        extractObservers(priceObservable).clear();
//        List<Indicator> indicatorsFromConfiguration = bgxStrategyMain.createIndicatorsFromConfiguration(falseIndicators);
//        bgxStrategyMain.addIndicatorsToObservable(priceObservable, indicatorsFromConfiguration);
//        CopyOnWriteArrayList<Observer> observers = extractObservers(priceObservable);
//
//        assertEquals(falseIndicators.size(), observers.size());
//        assertEqualsFalseIndicatorsToObservers(observers);
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
        Observer observer = bgxStrategyMain.setPositionObserver(brokerGatewayMock, entryStrategyMock, orderStrategyMock, configurationMock, exitStrategyMock);
        Object entryStrategy = commonMembers.extractFieldObject(observer, "entryStrategy");

        assertNotNull(entryStrategy);
        assertEquals(StandardEntryStrategy.class, entryStrategy.getClass());
    }

    @Test
    public void WhenCallSetPositionObserverThenHeMustHaveOrderStrategy(){
        Observer observer = bgxStrategyMain.setPositionObserver(brokerGatewayMock, entryStrategyMock, orderStrategyMock, configurationMock, exitStrategyMock);
        Object orderStrategy = commonMembers.extractFieldObject(observer, "orderStrategy");

        assertNotNull(orderStrategy);
        assertEquals(StandardOrderStrategy.class, orderStrategy.getClass());
    }

    @Test
    public void WhenCallSetPositionObserverThenHeMustHaveConfiguration(){
        Observer observer = bgxStrategyMain.setPositionObserver(brokerGatewayMock, entryStrategyMock, orderStrategyMock, configurationMock, exitStrategyMock);
        Object configuration = commonMembers.extractFieldObject(observer, "configuration");

        assertNotNull(configuration);
        assertEquals(BGXConfigurationImpl.class, configuration.getClass());
    }

    @Test
    public void WhenInstantiateOrderStrategyItMustBeCorrectType(){
        Object orderStrategy = commonMembers.extractFieldObject(bgxStrategyMain, "orderStrategy");

        assertNotNull(orderStrategy);
        assertEquals(StandardOrderStrategy.class, orderStrategy.getClass());
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
            Map<String, String> falseIndicator = falseIndicators.get(i);
            Indicator indicator = (Indicator) commonMembers.extractFieldObject(observers.get(i), "indicator");
            assertEqualsFalseIndicatorToReal(falseIndicator, indicator);
        }
    }

    private void assertIndicatorsEquality(List<Indicator> indicators) {
        for (int i = 0; i <falseIndicators.size() ; i++) {
            Map<String, String> falseIndicator = falseIndicators.get(i);
            assertEqualsFalseIndicatorToReal(falseIndicator, indicators.get(i));
        }
    }

    private void assertEqualsFalseIndicatorToReal(Map<String, String> falseIndicator, Indicator indicator) {
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

    private void assertGranularity(Map<String, String> falseIndicator, Indicator indicator) {
        CandleGranularity actual = (CandleGranularity) commonMembers.extractFieldObject(indicator, "granularity");
        String expected = falseIndicator.get("granularity");
        assertEquals(expected.toUpperCase(), actual.toString());
    }

    private void assertCandlePriceType(Map<String, String> falseIndicator, Indicator indicator) {
        CandlePriceType actual = (CandlePriceType) commonMembers.extractFieldObject(indicator, "candlePriceType");
        String expected = falseIndicator.get("candlePriceType");
        assertEquals(expected.toUpperCase(), actual.toString());
    }

    private void assertPeriod(Map<String, String> falseIndicator, Indicator indicator) {
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

    private Map<String, String> createFalseIndicator(String... args) {
        Map<String, String> indicator = new HashMap<>();
        indicator.put("type", args[0]);
        indicator.put("period", args[1]);
        indicator.put("candlePriceType", args[2]);
        indicator.put("granularity", args[3]);
        if(!args[0].toLowerCase().trim().contains("rsi")){
            indicator.put("position", args[4]);
        }
        return indicator;
    }

}