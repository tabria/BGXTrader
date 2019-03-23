package trader.indicator.rsi;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.candlestick.candle.CandlePriceType;
import trader.connector.BaseConnector;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;
import trader.exception.OutOfBoundaryException;
import trader.exception.WrongIndicatorSettingsException;
import trader.indicator.Indicator;
import trader.indicator.IndicatorUpdateHelper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.strategy.BGXStrategy.configuration.StrategyConfig.RSI_SETTINGS;

public class RSIBuilderTest {

    private static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final CandlePriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlePriceType.CLOSE;
    private static final long NEW_CANDLESTICKS_QUANTITY = 17L;

    private RSIBuilder builder;
    private BaseConnector baseConnector;
    private CommonTestClassMembers commonMembers;
    private IndicatorUpdateHelper indicatorUpdateHelper;

    @Before
    public void before(){
        baseConnector = mock(BaseConnector.class);
        commonMembers = new CommonTestClassMembers();
        indicatorUpdateHelper = new IndicatorUpdateHelper(DEFAULT_CANDLESTICK_PRICE_TYPE);
        indicatorUpdateHelper.fillCandlestickList();
        builder = new RSIBuilder(baseConnector);

    }

    @Test(expected = NoSuchConnectorException.class)
    public void WhenCreateRSIBuilderWithNullApiConnector_Exception(){
        new RSIBuilder(null);
    }

    @Test
    public void testCreateNewBuilderWithDefaultPeriod() {
        long actual = (long) commonMembers.extractFieldObject(builder, "indicatorPeriod");

        assertEquals(DEFAULT_INDICATOR_PERIOD, actual);
    }

    @Test
    public void testCreateNewBuilderWithDefaultCandlestickPriceType() {
        CandlePriceType actual = (CandlePriceType) commonMembers.extractFieldObject(builder, "candlePriceType");

        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, actual);
    }

    @Test
    public void WhenCallSetPeriod_ReturnCurrentObject(){
        assertEquals(builder, builder.setPeriod(NEW_CANDLESTICKS_QUANTITY));
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueGreaterLessThanMINPeriod_Exception() {
        builder.setPeriod(0);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueGreaterThanMAXPeriod_Exception() {
        builder.setPeriod(1001L);
    }

    @Test
    public void callSetPeriodWithCorrectValue_SuccessfulUpdate() {
        long expected = 11L;
        builder.setPeriod(expected);
        long actual = (long) commonMembers.extractFieldObject(builder, "indicatorPeriod");

        assertEquals(expected, actual);
    }

    @Test
    public void WhenCallSetCandlestickPriceType_ReturnCurrentObject(){
        assertEquals(builder, builder.setCandlePriceType(CandlePriceType.CLOSE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetCandlestickPriceTypeWithNull_Exception(){
        builder.setCandlePriceType(null);
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectValue_SuccessfulUpdate(){
        CandlePriceType expected = CandlePriceType.OPEN;
        builder.setCandlePriceType(expected);
        CandlePriceType actual = (CandlePriceType) commonMembers.extractFieldObject(builder, "candlePriceType");

        assertEquals(expected, actual);
    }

    @Test
    public void WhenCallBuild_SuccessfulBuild(){
        when(baseConnector.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());
        Indicator rsi = builder.setPeriod(13).build();
        String rsiName = rsi.getClass().getSimpleName();

        assertEquals("The object is not RSI Indicator","RelativeStrengthIndex", rsiName);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void buildRSI_ExternalSettingsNotDefaultQuantity(){
        new RSIBuilder(baseConnector).build(new String[]{""});
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void buildRSIWithNullExternalSettings(){
        new RSIBuilder(baseConnector).build(null);
    }

    @Test
    public void buildRSIWithExternalSettings() {
        indicatorUpdateHelper.candlestickList.add(indicatorUpdateHelper.createCandlestickMock());
        when(baseConnector.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());

        Indicator rsi = new RSIBuilder(baseConnector).build(RSI_SETTINGS);
        long actualIndicatorPeriod = (long) commonMembers
                .extractFieldObject(rsi, "indicatorPeriod");
        CandlePriceType actualCandlePriceType = (CandlePriceType) commonMembers.extractFieldObject(rsi, "candlePriceType");

        assertEquals(RelativeStrengthIndex.class, rsi.getClass());
        assertEquals(Long.parseLong(RSI_SETTINGS[0]), actualIndicatorPeriod );
        assertEquals(CandlePriceType.valueOf(RSI_SETTINGS[1]), actualCandlePriceType);
    }
}