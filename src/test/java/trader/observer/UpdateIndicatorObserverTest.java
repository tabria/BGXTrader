package trader.observer;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.strategy.TradingStrategyConfiguration;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.entity.price.Price;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class UpdateIndicatorObserverTest extends BaseObserverTest {

    private static final String QUANTITY = "quantity";
    private static final String INDICATOR = "indicator";
    private static final CandleGranularity GRANULARITY_VALUE = CandleGranularity.M10;
    private static final String INSTRUMENT = "instrument";
    private static final String GRANULARITY = "granularity";
    private static final String SETTINGS = "settings";

    private Observer observer;
    private TradingStrategyConfiguration mockConfiguration;
    private Indicator mockMA;
    private Price mockPrice;

    @Before
    public void before(){
        super.before();
        this.mockConfiguration = mock(TradingStrategyConfiguration.class);
        setConfiguration();
        this.mockMA = mock(Indicator.class);
        setMockMA();
        mockPrice = mock(Price.class);
        this.observer = new UpdateIndicatorObserver(this.mockMA, mockConfiguration, brokerGatewayMock);

    }

    @Test(expected = NullArgumentException.class)
    public void whenCreateUpdateIndicatorControllerWithNullIndicator_ThrowException(){
       new UpdateIndicatorObserver(null, mockConfiguration, brokerGatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateUpdateIndicatorControllerWithNullConfiguration_Exception(){
        new UpdateIndicatorObserver(mockMA, null, brokerGatewayMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateUpdateIndicatorControllerWithNullBrokerGateway_Exception(){
        new UpdateIndicatorObserver(mockMA, mockConfiguration, null);
    }

    @Test
    public void whenCreateNewIndicatorObserver_IndicatorsMustMatch(){
        Indicator indicator = (Indicator) commonTestMembers.extractFieldObject(observer, INDICATOR);
        assertSame(mockMA, indicator);
    }

    @Test(expected = NullArgumentException.class)
    public void callUpdateObserverWithNullPrice_ThrowException() {
        observer.updateObserver(null);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testUpdateIndicatorControllerForCorrectExecution() {
        List<Candlestick> candlesticks = new ArrayList<>();
        exception.expect(RuntimeException.class);
       // exception.expectMessage("Update OK");
        when(brokerGatewayMock.getCandles(any(HashMap.class))).thenReturn(candlesticks);
        doThrow(RuntimeException.class).when(mockMA).updateIndicator(candlesticks);
        observer.updateObserver(mockPrice);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void WhenCreateUpdateIndicatorControllerSettingsMustNotNull(){
        HashMap<String, String> settings = (HashMap<String, String>) commonTestMembers.extractFieldObject(observer, SETTINGS);
        assertNotNull(settings);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void WhenCreateUpdateIndicatorControllerSettingsMustNotBeEmpty(){
        HashMap<String, String> settings = (HashMap<String, String>) commonTestMembers.extractFieldObject(observer, SETTINGS);
        assertNotNull(settings);
        assertNotEquals(0, settings.size());
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void CheckForCorrectFieldsInInitialSettings(){
        HashMap<String, String> settings = (HashMap<String, String>) commonTestMembers.extractFieldObject(observer, SETTINGS);

        assertInitialSettingsCorrectness(settings);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void WhenIndicatorValuesAreNotEmpty_SettingsMustHaveUpdateCandlesQuantity(){

        makeMockMAToHaveValues(BigDecimal.TEN);
        observer.updateObserver(mockPrice);
        HashMap<String, String> newSettings = (HashMap<String, String>) commonTestMembers.extractFieldObject(observer, SETTINGS);

        assertEquals(String.valueOf(UPDATE_QUANTITY), newSettings.get(QUANTITY));
    }

    private void setConfiguration() {
        when(mockConfiguration.getInstrument()).thenReturn(INSTRUMENT_VALUE);
        when(mockConfiguration.getInitialCandlesQuantity()).thenReturn(INITIAL_QUANTITY);
        when(mockConfiguration.getUpdateCandlesQuantity()).thenReturn(UPDATE_QUANTITY);
    }

    private List<BigDecimal> makeMockMAToHaveValues(BigDecimal... values){
        List<BigDecimal> mockMAValues = new ArrayList<>(Arrays.asList(values));
        when(mockMA.getValues()).thenReturn(mockMAValues);
        return mockMAValues;
    }

    private void assertInitialSettingsCorrectness(HashMap<String, String> settings) {
        assertTrue(settings.containsKey(INSTRUMENT));
        assertNotNull(settings.get(INSTRUMENT));
        assertEquals(INSTRUMENT_VALUE, settings.get(INSTRUMENT));
        assertTrue(settings.containsKey(QUANTITY));
        assertNotNull(settings.get(QUANTITY));
        assertEquals(String.valueOf(INITIAL_QUANTITY), settings.get(QUANTITY));
        assertTrue(settings.containsKey(GRANULARITY));
        assertNotNull(settings.get(GRANULARITY));
        assertEquals(GRANULARITY_VALUE.toString(), settings.get(GRANULARITY));
    }

    private void setMockMA() {
        when(mockMA.getGranularity()).thenReturn(GRANULARITY_VALUE);
    }
}