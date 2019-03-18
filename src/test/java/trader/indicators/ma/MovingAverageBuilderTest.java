package trader.indicators.ma;

import com.oanda.v20.instrument.*;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPIMock.OandaAPIMock;
import trader.indicators.Indicator;
import trader.indicators.enums.CandleGranularity;
import trader.indicators.enums.CandlestickPriceType;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.indicators.ma.enums.MAType.*;

public class MovingAverageBuilderTest {


    private static final String CANDLESTICK_PRICE_TYPE = "candlestickPriceType";
    private static final String MOVING_AVERAGE_TYPE = "maType";
    private static final String CANDLESTICK_QUANTITY = "candlestickQuantity";
    private static final String CANDLE_TIME_FRAME = "candleTimeFrame";
    private static final String MAX_CANDLESTICK_QUANTITY = "MAX_CANDLESTICK_QUANTITY";
    private static final String MIN_CANDLESTICK_QUANTITY = "MIN_CANDLESTICK_QUANTITY";

    private MovingAverageBuilder builder;


    
    @Before
    public void setUp() throws Exception {

        OandaAPIMock oandaAPIMock = new OandaAPIMock();
        when(oandaAPIMock.getMockResponse().getCandles()).thenReturn(new ArrayList<>());
        when(oandaAPIMock.getContext().instrument.candles(any(InstrumentCandlesRequest.class)))
                .thenReturn(oandaAPIMock.getMockResponse());

        this.builder = new MovingAverageBuilder(oandaAPIMock.getContext());
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateMABuilderWithNullContext_Exception(){
        new MovingAverageBuilder(null);
    }

    @Test(expected = NullPointerException.class)
    public void whenCallSetCandleTimeFrameWithNull_Exception() {
        this.builder = this.builder.setCandleTimeFrame(null);
    }

    @Test
    public void testForCorrectCandleTimeFrame() throws NoSuchFieldException, IllegalAccessException {
        this.builder.setCandleTimeFrame(CandleGranularity.M15);

        assertEquals(CandlestickGranularity.M15.toString(), extractFieldObject(CANDLE_TIME_FRAME).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCallSetCandlesQuantityWithLessThanMinimumCandlesQuantity_Exception() throws NoSuchFieldException, IllegalAccessException {
        long minCandlesQuantity = (long) extractFieldObject(MIN_CANDLESTICK_QUANTITY);
        this.builder.setCandlesQuantity(minCandlesQuantity - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCallSetCandlesQuantityWithMoreThanMaxCandleQuantity_Exception() throws NoSuchFieldException, IllegalAccessException {
        long maxCandlesQuantity = (long) extractFieldObject(MAX_CANDLESTICK_QUANTITY);
        this.builder.setCandlesQuantity(maxCandlesQuantity + 1);
    }

    @Test
    public void testForCorrectCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        long expected = 11L;
        this.builder.setCandlesQuantity(expected);

        assertEquals(expected, extractFieldObject(CANDLESTICK_QUANTITY));
    }

    @Test(expected = NullPointerException.class)
    public void whenCallSetCandlestickPriceTypeWithNull_Exception() {
        this.builder.setCandlestickPriceType(null);
    }

    @Test
    public void callSetCandlestickPriceTypeWithMediumType() throws NoSuchFieldException, IllegalAccessException {
        this.builder.setCandlestickPriceType(CandlestickPriceType.MEDIAN);

        assertSame(CandlestickPriceType.MEDIAN, extractFieldObject(CANDLESTICK_PRICE_TYPE));
    }

    @Test(expected = NullPointerException.class)
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
        Indicator sma = this.builder.setMAType(SIMPLE).build();
        Indicator ema = this.builder.setMAType(EXPONENTIAL).build();
        Indicator wma = this.builder.setMAType(WEIGHTED).build();

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