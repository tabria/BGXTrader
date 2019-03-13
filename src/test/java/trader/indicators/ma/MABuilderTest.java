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

public class MABuilderTest {


    private MABuilder builder;
    private OandaAPIMock oandaAPIMock;
    private long minCandlesQuantity;
    private long maxCandlesQuantity;

    
    @Before
    public void setUp() throws Exception {

        oandaAPIMock = new OandaAPIMock();
        when(oandaAPIMock.getMockResponse().getCandles()).thenReturn(new ArrayList<>());
        when(oandaAPIMock.getContext().instrument.candles(any(InstrumentCandlesRequest.class)))
                .thenReturn(oandaAPIMock.getMockResponse());

        this.builder = new MABuilder(oandaAPIMock.getContext());
        this.minCandlesQuantity = extractMinCandlesQuantity();
        this.maxCandlesQuantity = extractMaxCandlesQuantity();
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateMABuilderWithNullContext_Exception(){
        new MABuilder(null);
    }

    @Test(expected = NullPointerException.class)
    public void whenCallSetCandleTimeFrameWithNull_Exception() {
        this.builder = this.builder.setCandleTimeFrame(null);
    }

    @Test
    public void callSetCandleTimeFrame() throws NoSuchFieldException, IllegalAccessException {
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
    public void callSetCandleQuantity() throws NoSuchFieldException, IllegalAccessException {
        long expected = 11L;
        this.builder.setCandlesQuantity(expected);

        assertEquals(expected, extractCandlesQuantity());
    }

    @Test(expected = NullPointerException.class)
    public void whenCallSetCandlestickPriceWithNull_Exception() {
        this.builder.setCandlestickPriceType(null);
    }

    @Test
    public void callSetCandlestickPrice() throws NoSuchFieldException, IllegalAccessException {
        CandlestickPriceType expected = CandlestickPriceType.MEDIAN;
        this.builder.setCandlestickPriceType(expected);

        Field field = this.builder.getClass().getDeclaredField("candlestickPriceType");
        field.setAccessible(true);
        CandlestickPriceType result = (CandlestickPriceType) field.get(this.builder);

        assertSame(expected, result);
    }

    @Test
    public void WhenSetAppliedPriceThenReturnSameObject(){
        MABuilder builderObj = this.builder.setCandlestickPriceType(CandlestickPriceType.CLOSE);

        assertEquals(this.builder, builderObj);
    }

    /**
     * Test MAType
     */
    @Test(expected = NullPointerException.class)
    public void WhenSetMATypeWithNullThenException() {
        this.builder.setMAType(null);
    }

    @Test
    public void WhenSetMATypeWithCorrectValueThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {
        MAType expected = EXPONENTIAL;
        this.builder.setMAType(expected);

        Field field = this.builder.getClass().getDeclaredField("maType");
        field.setAccessible(true);
        MAType result = (MAType) field.get(this.builder);

        assertSame(expected, result);
    }

    @Test
    public void WhenSetMATypeThenReturnSameObject(){
        MABuilder builderObj = this.builder.setMAType(SIMPLE);
        assertEquals(this.builder, builderObj);
    }

    /**
     * Test build
     */
    @Test
    public void WhenBuildThenReturnCorrectObject()  {

        Indicator sma = this.builder.setMAType(MAType.SIMPLE).build();
        String smaName = sma.getClass().getSimpleName();

        Indicator ema = this.builder.setMAType(EXPONENTIAL).build();
        String emaName = ema.getClass().getSimpleName();

        Indicator wma = this.builder.setMAType(WEIGHTED).build();
        String wmaName = wma.getClass().getSimpleName();


        assertEquals("The object is not SMA","SimpleMA", smaName);
        assertEquals("The object is not EMA","ExponentialMA", emaName);
        assertEquals("The object is not WMA","WeightedMA", wmaName);
    }

    private long extractMinCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("MIN_PERIOD");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

    private long extractMaxCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("MAX_PERIOD");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

    private CandlestickGranularity extractCandlestickGranularity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("candleTimeFrame");
        field.setAccessible(true);
        return (CandlestickGranularity) field.get(this.builder);
    }

    private long extractCandlesQuantity() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("period");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

//    private void setMockRequest() throws NoSuchFieldException, IllegalAccessException {
//        Field request = this.builder.getClass().getDeclaredField("request");
//        request.setAccessible(true);
//        request.set(this.builder, this.mockRequest);
//    }
}