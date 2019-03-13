package trader.indicators.ma;

import com.oanda.v20.instrument.*;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPI.OandaAPIMock;
import trader.indicators.Indicator;
import trader.indicators.enums.CandlestickPriceType;
import trader.indicators.ma.enums.MAType;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.indicators.ma.enums.MAType.*;

public class MovingAverageBuilderTest {


    private MovingAverageBuilder builder;
    private long minCandlesQuantity;
    private long maxCandlesQuantity;

    
    @Before
    public void setUp() throws Exception {

        OandaAPIMock oandaAPIMock = new OandaAPIMock();
        when(oandaAPIMock.getMockResponse().getCandles()).thenReturn(new ArrayList<>());
        when(oandaAPIMock.getContext().instrument.candles(any(InstrumentCandlesRequest.class)))
                .thenReturn(oandaAPIMock.getMockResponse());

        this.builder = new MovingAverageBuilder(oandaAPIMock.getContext());
        this.minCandlesQuantity = extractMinCandlesQuantity();
        this.maxCandlesQuantity = extractMaxCandlesQuantity();
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
        this.builder.setCandleTimeFrame(CandlestickGranularity.M15);

        assertSame(CandlestickGranularity.M15, extractCandlestickGranularity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCallSetCandlesQuantityWithLessThanMinimumCandlesQuantity_Exception() {
        this.builder.setCandlesQuantity(this.minCandlesQuantity - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCallSetCandlesQuantityWithMoreThanMaxCandleQuantity_Exception(){
        this.builder.setCandlesQuantity(this.maxCandlesQuantity + 1);
    }

    @Test
    public void testForCorrectCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        long expected = 11L;
        this.builder.setCandlesQuantity(expected);

        assertEquals(expected, extractCandlesQuantity());
    }

    @Test(expected = NullPointerException.class)
    public void whenCallSetCandlestickPriceTypeWithNull_Exception() {
        this.builder.setCandlestickPriceType(null);
    }

    @Test
    public void callSetCandlestickPriceTypeWithMediumType() throws NoSuchFieldException, IllegalAccessException {
        this.builder.setCandlestickPriceType(CandlestickPriceType.MEDIAN);

        assertSame(CandlestickPriceType.MEDIAN, extractCandlestickPriceType());
    }

    @Test(expected = NullPointerException.class)
    public void callSetMATypeWithNull_Exception() {
        this.builder.setMAType(null);
    }

    @Test
    public void testForCorrectMovingAverageType() throws NoSuchFieldException, IllegalAccessException {
        this.builder.setMAType(EXPONENTIAL);

        assertSame(EXPONENTIAL, extractMaType());
    }

    @Test
    public void buildMovingAverage()  {

        Indicator sma = this.builder.setMAType(MAType.SIMPLE).build();
        Indicator ema = this.builder.setMAType(EXPONENTIAL).build();
        Indicator wma = this.builder.setMAType(WEIGHTED).build();


        assertEquals("The object is not SMA","SimpleMA", sma.getClass().getSimpleName());
        assertEquals("The object is not EMA","ExponentialMA", ema.getClass().getSimpleName());
        assertEquals("The object is not WMA","WeightedMA", wma.getClass().getSimpleName());
    }

    private long extractMinCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("MIN_CANDLESTICK_QUANTITY");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

    private long extractMaxCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("MAX_CANDLESTICK_QUANTITY");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

    private CandlestickGranularity extractCandlestickGranularity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("candleTimeFrame");
        field.setAccessible(true);
        return (CandlestickGranularity) field.get(this.builder);
    }

    private long extractCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("candlestickQuantity");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

    private CandlestickPriceType extractCandlestickPriceType() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("candlestickPriceType");
        field.setAccessible(true);
        return (CandlestickPriceType) field.get(this.builder);
    }

    private MAType extractMaType() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("maType");
        field.setAccessible(true);
        return (MAType) field.get(this.builder);
    }
}