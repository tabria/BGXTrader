package trader.indicator.rsi;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.candle.Candlestick;
import trader.candle.CandlestickPriceType;
import trader.connector.ApiConnector;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;
import trader.exception.OutOfBoundaryException;
import trader.exception.WrongIndicatorSettingsException;
import trader.indicator.Indicator;
import trader.indicator.IndicatorUpdateHelper;
import trader.indicator.ma.SimpleMovingAverage;
import trader.strategy.BGXStrategy.StrategyConfig;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.strategy.BGXStrategy.StrategyConfig.RSI_SETTINGS;

public class RSIBuilderTest {

    private static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final CandlestickPriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlestickPriceType.CLOSE;
    private static final long NEW_CANDLESTICKS_QUANTITY = 17L;

    private RSIBuilder builder;
    private ApiConnector apiConnector;
    private CommonTestClassMembers commonMembers;
    private IndicatorUpdateHelper indicatorUpdateHelper;

    @Before
    public void before(){
        apiConnector = mock(ApiConnector.class);
        commonMembers = new CommonTestClassMembers();
        indicatorUpdateHelper = new IndicatorUpdateHelper(DEFAULT_CANDLESTICK_PRICE_TYPE);
        indicatorUpdateHelper.fillCandlestickList();
        builder = new RSIBuilder(apiConnector);

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
        CandlestickPriceType actual = (CandlestickPriceType) commonMembers.extractFieldObject(builder, "candlestickPriceType");

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
        assertEquals(builder, builder.setCandlestickPriceType(CandlestickPriceType.CLOSE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetCandlestickPriceTypeWithNull_Exception(){
        builder.setCandlestickPriceType(null);
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectValue_SuccessfulUpdate(){
        CandlestickPriceType expected = CandlestickPriceType.OPEN;
        builder.setCandlestickPriceType(expected);
        CandlestickPriceType actual = (CandlestickPriceType) commonMembers.extractFieldObject(builder, "candlestickPriceType");

        assertEquals(expected, actual);
    }

    @Test
    public void WhenCallBuild_SuccessfulBuild(){
        when(apiConnector.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());
        Indicator rsi = builder.setPeriod(13).build();
        String rsiName = rsi.getClass().getSimpleName();

        assertEquals("The object is not RSI Indicator","RelativeStrengthIndex", rsiName);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void buildRSI_ExternalSettingsNotDefaultQuantity(){
        new RSIBuilder(apiConnector).build(new String[]{""});
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void buildRSIWithNullExternalSettings(){
        new RSIBuilder(apiConnector).build(null);
    }

    @Test
    public void buildRSIWithExternalSettings() {
        indicatorUpdateHelper.candlestickList.add(indicatorUpdateHelper.createCandlestickMock());
        when(apiConnector.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());

        Indicator rsi = new RSIBuilder(apiConnector).build(RSI_SETTINGS);
        long actualIndicatorPeriod = (long) commonMembers
                .extractFieldObject(rsi, "indicatorPeriod");
        CandlestickPriceType actualCandlestickPriceType = (CandlestickPriceType) commonMembers.extractFieldObject(rsi, "candlestickPriceType");

        assertEquals(RelativeStrengthIndex.class, rsi.getClass());
        assertEquals(Long.parseLong(RSI_SETTINGS[0]), actualIndicatorPeriod );
        assertEquals(CandlestickPriceType.valueOf(RSI_SETTINGS[1]), actualCandlestickPriceType);
    }
}