package trader.controller;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.CommonTestClassMembers;
import trader.configuration.TradingStrategyConfiguration;
import trader.controller.enums.SettingsFieldNames;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.price.Price;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class IndicatorObserverTest {

    private static final String QUANTITY = "quantity";
    private static final String INDICATOR = "indicator";
    private static final String INSTRUMENT_VALUE = "EUR_USD";
    private static final long INITIAL_QUANTITY = 100L;
    private static final long UPDATE_QUANTITY = 2L;
    private static final CandleGranularity GRANULARITY_VALUE = CandleGranularity.M10;
    private static final String INSTRUMENT = "instrument";
    private static final String GRANULARITY = "granularity";
    private static final String SETTINGS = "settings";

    private Observer observer;
    private Indicator mockMA;
    private UpdateIndicatorController mockController;
    private TradingStrategyConfiguration mockConfiguration;
    private Price mockPrice;
    private CommonTestClassMembers commonTestMembers;

    @Before
    public void before(){

        this.mockMA = mock(Indicator.class);
        setMockMA();
        this.mockController = mock(UpdateIndicatorController.class);
        this.mockConfiguration = mock(TradingStrategyConfiguration.class);
        setConfiguration();
        this.observer = IndicatorObserver.create(this.mockMA, mockController, mockConfiguration);
        mockPrice = mock(Price.class);
        commonTestMembers = new CommonTestClassMembers();
    }

    @Test(expected = NullArgumentException.class)
    public void whenCreateIndicatorObserverWithNullIndicator_ThrowException(){
        IndicatorObserver.create(null, mockController, mockConfiguration);



    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateIndicatorObserverWithNullUpdateIndicatorController_Exception(){
        IndicatorObserver.create(mockMA, null, mockConfiguration);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateIndicatorObserverWithNullConfiguration_Exception(){
        IndicatorObserver.create(mockMA, mockController, null);
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
    public void testUpdateObserverForCorrectExecution() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Update OK");

        doThrow(new RuntimeException("Update OK")).when(mockController).execute(any(HashMap.class));
        observer.updateObserver(mockPrice);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void WhenCreateObserverSettingsMustNotNull(){
        HashMap<String, String> settings = (HashMap<String, String>) commonTestMembers.extractFieldObject(observer, SETTINGS);
        assertNotNull(settings);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void WhenCreateObserverSettingsMustNotBeEmpty(){
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

    private List<BigDecimal> makeMockMAToHaveValues(BigDecimal... values){
        List<BigDecimal> mockMAValues = new ArrayList<>(Arrays.asList(values));
        when(mockMA.getValues()).thenReturn(mockMAValues);
        setControllerToReturnNull();
        return mockMAValues;
    }

    private void setControllerToReturnNull() {
        when(mockController.execute(any(HashMap.class))).thenReturn(null);
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

    private void setConfiguration() {
        when(mockConfiguration.getInstrument()).thenReturn(INSTRUMENT_VALUE);
        when(mockConfiguration.getInitialCandlesQuantity()).thenReturn(INITIAL_QUANTITY);
        when(mockConfiguration.getUpdateCandlesQuantity()).thenReturn(UPDATE_QUANTITY);
    }
}