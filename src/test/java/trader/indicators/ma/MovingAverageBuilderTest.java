package trader.indicators.ma;

import com.oanda.v20.instrument.*;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.candle.CandlesUpdater;
import trader.candle.Candlestick;
import trader.connectors.ApiConnector;
import trader.exceptions.NoSuchConnectorException;
import trader.exceptions.NullArgumentException;
import trader.exceptions.OutOfBoundaryException;
import trader.indicators.Indicator;
import trader.candle.CandlestickPriceType;
import trader.indicators.IndicatorUpdateHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static trader.indicators.ma.enums.MAType.*;

public class MovingAverageBuilderTest {


    private static final String CANDLESTICK_PRICE_TYPE = "candlestickPriceType";
    private static final String MOVING_AVERAGE_TYPE = "maType";
    private static final String PERIOD = "indicatorPeriod";
    private static final String MAX_PERIOD = "MAX_INDICATOR_PERIOD";
    private static final String MIN_PERIOD = "MIN_INDICATOR_PERIOD";

    private MovingAverageBuilder builder;
    private ApiConnector mockApiConnectior;
    protected CandlestickPriceType candlestickPriceType = CandlestickPriceType.CLOSE;
    private IndicatorUpdateHelper indicatorUpdateHelper;

    @Before
    public void setUp() throws Exception {

        OandaAPIMockInstrument oandaInstrument = new OandaAPIMockInstrument();
        when(oandaInstrument.getMockResponse().getCandles())
                .thenReturn(new ArrayList<>());
        when(oandaInstrument.getContext().instrument.candles(any(InstrumentCandlesRequest.class)))
                .thenReturn(oandaInstrument.getMockResponse());
        mockApiConnectior = mock(ApiConnector.class);
        this.indicatorUpdateHelper = new IndicatorUpdateHelper(this.candlestickPriceType);
        this.indicatorUpdateHelper.fillCandlestickList();
        this.builder = new MovingAverageBuilder(mockApiConnectior);

    }

    @Test(expected = NoSuchConnectorException.class)
    public void whenCreateMABuilderWithNullApiConnector_Exception(){
        new MovingAverageBuilder(null);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void whenCallSetPeriodWithLessThanMinimumPeriod_Exception() throws NoSuchFieldException, IllegalAccessException {
        long minPeriod = (long) extractFieldObject(MIN_PERIOD);
        this.builder.setPeriod(minPeriod - 1);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void whenCallSetPeriodWithMoreThanMaxPeriod_Exception() throws NoSuchFieldException, IllegalAccessException {
        long maxPeriod = (long) extractFieldObject(MAX_PERIOD);
        this.builder.setPeriod(maxPeriod + 1);
    }

    @Test
    public void testForCorrectPeriod() throws NoSuchFieldException, IllegalAccessException {
        long expected = 11L;
        this.builder.setPeriod(expected);

        assertEquals(expected, extractFieldObject(PERIOD));
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallSetCandlestickPriceTypeWithNull_Exception() {
        this.builder.setCandlestickPriceType(null);
    }

    @Test
    public void callSetCandlestickPriceTypeWithMediumType() throws NoSuchFieldException, IllegalAccessException {
        this.builder.setCandlestickPriceType(CandlestickPriceType.MEDIAN);

        assertSame(CandlestickPriceType.MEDIAN, extractFieldObject(CANDLESTICK_PRICE_TYPE));
    }

    @Test(expected = NullArgumentException.class)
    public void callSetMATypeWithNull_Exception() {
        this.builder.setMAType(null);
    }

    @Test
    public void testForCorrectMovingAverageType() throws NoSuchFieldException, IllegalAccessException {
        this.builder.setMAType(EXPONENTIAL);

        assertSame(EXPONENTIAL, extractFieldObject(MOVING_AVERAGE_TYPE));
    }

    @Test
    public void buildMovingAverage()  {
        when(mockApiConnectior.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());
        Indicator sma = this.builder.setMAType(SIMPLE).setPeriod(7).build();
        Indicator ema = this.builder.setMAType(EXPONENTIAL).setPeriod(7).build();
        Indicator wma = this.builder.setMAType(WEIGHTED).setPeriod(7).build();

        assertEquals("The object is not SMA","SimpleMovingAverage", sma.getClass().getSimpleName());
        assertEquals("The object is not EMA","ExponentialMovingAverage", ema.getClass().getSimpleName());
        assertEquals("The object is not WMA","WeightedMovingAverage", wma.getClass().getSimpleName());
    }

    private Object extractFieldObject(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(this.builder);
    }
}