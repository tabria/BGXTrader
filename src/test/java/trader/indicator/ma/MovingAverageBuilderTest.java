package trader.indicator.ma;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.connector.ApiConnector;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;
import trader.exception.OutOfBoundaryException;
import trader.exception.WrongIndicatorSettingsException;
import trader.indicator.Indicator;
import trader.candle.CandlestickPriceType;
import trader.indicator.IndicatorUpdateHelper;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static trader.indicator.ma.enums.MAType.*;
import static trader.strategy.BGXStrategy.StrategyConfig.*;

public class MovingAverageBuilderTest {


    private static final String CANDLESTICK_PRICE_TYPE = "candlestickPriceType";
    private static final String MOVING_AVERAGE_TYPE = "maType";
    private static final String PERIOD = "indicatorPeriod";
    private static final String MAX_PERIOD = "MAX_INDICATOR_PERIOD";
    private static final String MIN_PERIOD = "MIN_INDICATOR_PERIOD";

    private MovingAverageBuilder builder;
    private ApiConnector mockApiConnector;
    private CandlestickPriceType candlestickPriceType = CandlestickPriceType.CLOSE;
    private IndicatorUpdateHelper indicatorUpdateHelper;
    private CommonTestClassMembers commonClassMembers;

    @Before
    public void setUp() {

        mockApiConnector = mock(ApiConnector.class);
        this.indicatorUpdateHelper = new IndicatorUpdateHelper(this.candlestickPriceType);
        this.indicatorUpdateHelper.fillCandlestickList();
        commonClassMembers = new CommonTestClassMembers();
        this.builder = new MovingAverageBuilder(mockApiConnector);
    }

    @Test(expected = NoSuchConnectorException.class)
    public void whenCreateMABuilderWithNullApiConnector_Exception(){
        new MovingAverageBuilder(null);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void whenCallSetPeriodWithLessThanMinimumPeriod_Exception() {
        long minPeriod = (long) commonClassMembers.extractFieldObject(builder, MIN_PERIOD);
        this.builder.setPeriod(minPeriod - 1);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void whenCallSetPeriodWithMoreThanMaxPeriod_Exception() {
        long maxPeriod = (long) commonClassMembers.extractFieldObject(builder, MAX_PERIOD);
        this.builder.setPeriod(maxPeriod + 1);
    }

    @Test
    public void testForCorrectPeriod(){
        long expected = 11L;
        this.builder.setPeriod(expected);

        assertEquals(expected, commonClassMembers.extractFieldObject(builder, PERIOD));
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallSetCandlestickPriceTypeWithNull_Exception() {
        this.builder.setCandlestickPriceType(null);
    }

    @Test
    public void callSetCandlestickPriceTypeWithMediumType() {
        this.builder.setCandlestickPriceType(CandlestickPriceType.MEDIAN);

        assertSame(CandlestickPriceType.MEDIAN, commonClassMembers.extractFieldObject(builder, CANDLESTICK_PRICE_TYPE));
    }

    @Test(expected = NullArgumentException.class)
    public void callSetMATypeWithNull_Exception() {
        this.builder.setMAType(null);
    }

    @Test
    public void testForCorrectMovingAverageType(){
        this.builder.setMAType(EXPONENTIAL);

        assertSame(EXPONENTIAL, commonClassMembers.extractFieldObject(builder, MOVING_AVERAGE_TYPE));
    }

    @Test
    public void buildMovingAverage()  {
        when(mockApiConnector.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());
        Indicator sma = this.builder.setMAType(SIMPLE).setPeriod(7).build();
        Indicator ema = this.builder.setMAType(EXPONENTIAL).setPeriod(7).build();
        Indicator wma = this.builder.setMAType(WEIGHTED).setPeriod(7).build();

        assertEquals("The object is not SMA","SimpleMovingAverage", sma.getClass().getSimpleName());
        assertEquals("The object is not EMA","ExponentialMovingAverage", ema.getClass().getSimpleName());
        assertEquals("The object is not WMA","WeightedMovingAverage", wma.getClass().getSimpleName());
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void buildMovingAverage_ExternalSettingsNotDefaultQuantity(){
        new MovingAverageBuilder(mockApiConnector).build(new String[]{""});
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void buildMovingAverageWithNullExternalSettings(){
        new MovingAverageBuilder(mockApiConnector).build(null);
    }

    @Test
    public void buildMovingAverageWithExternalSettings(){
        when(mockApiConnector.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());
        Indicator sma = new MovingAverageBuilder(mockApiConnector)
                .build(PRICE_SMA_SETTINGS);

        long actualIndicatorPeriod = (long) commonClassMembers.extractFieldObject(sma, "indicatorPeriod");
        CandlestickPriceType actualCandlestickPriceType = (CandlestickPriceType) commonClassMembers.extractFieldObject(sma, "candlestickPriceType");

        assertEquals(SimpleMovingAverage.class, sma.getClass());
        assertEquals(Long.parseLong(PRICE_SMA_SETTINGS[0]), actualIndicatorPeriod );
        assertEquals(CandlestickPriceType.valueOf(PRICE_SMA_SETTINGS[1]), actualCandlestickPriceType);
    }
}