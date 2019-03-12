package trader.indicators.ma;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.*;
import org.junit.Before;
import org.junit.Test;
import trader.indicators.Indicator;
import trader.indicators.enums.CandlestickPrice;
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
    private Context mockContext;
    private long min_period;
    private long max_period;
    private InstrumentCandlesRequest mockRequest;
    private InstrumentCandlesResponse mockResponse;

    @Before
    public void setUp() throws Exception {

       // this.mockCandlestickList = mock(List.class);
       // when(this.mockCandlestickList.get(0)).thenReturn(this.mockCandlestick);

        this.mockRequest = mock(InstrumentCandlesRequest.class);

        this.mockResponse = mock(InstrumentCandlesResponse.class);
        when(this.mockResponse.getCandles()).thenReturn(new ArrayList<>());

        this.mockContext = mock(Context.class);
        this.mockContext.instrument = mock(InstrumentContext.class);
        when(this.mockContext.instrument.candles(any(InstrumentCandlesRequest.class))).thenReturn(this.mockResponse);

        this.builder = new MABuilder(this.mockContext);

        this.min_period = getMinPeriod();
        this.max_period = getMaxPeriod();

        //setMockRequest();
    }


    @Test(expected = NullPointerException.class)
    public void WhenCreateMABuilderWithNullContextThenException(){
        new MABuilder(null);
    }

    /**
     * Test setCandleTimeFrame
     */
    @Test(expected = NullPointerException.class)
    public void WhenSetCandleTimeFrameWithNullThenException() {
        this.builder = this.builder.setCandleTimeFrame(null);
    }

    @Test
    public void WhenSetCandleTimeFrameWithCorrectValueThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {
        CandlestickGranularity expected = CandlestickGranularity.M15;
        this.builder.setCandleTimeFrame(expected);
        Field field = this.builder.getClass().getDeclaredField("candleTimeFrame");
        field.setAccessible(true);
        CandlestickGranularity result = (CandlestickGranularity) field.get(this.builder);

        assertSame(expected, result);
    }

    @Test
    public void WhenCallSetCandleTimeFrameThenMustReturnSameBuilderObject(){
        MABuilder builderObj2 = this.builder.setCandleTimeFrame(CandlestickGranularity.H8);
        assertSame(this.builder, builderObj2);
    }

    /**
     * Test setPeriod
     */
    @Test(expected = IllegalArgumentException.class)
    public void WhenSetPeriodWithLessThanMIN_PERIODThenException() {
        this.builder.setPeriod(this.min_period - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenSetPeriodWithMoreThanMAX_PERIODThenException(){
        this.builder.setPeriod(this.max_period + 1);
    }

    @Test
    public void WhenSetPeriodBetweenMIN_PERIODAndMAX_PERIODThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {
        long expected = 11L;
        this.builder.setPeriod(expected);

        Field field = this.builder.getClass().getDeclaredField("period");
        field.setAccessible(true);
        long result = (long) field.get(this.builder);

        assertEquals(expected, result);
    }

    @Test
    public void WhenSetPeriodThenReturnSameObject(){
        MABuilder builderObj = this.builder.setPeriod(11);

        assertSame(this.builder, builderObj);
    }

    /**
     * Test appliedPrice
     */

    @Test(expected = NullPointerException.class)
    public void WhenSetAppliedPriceWithNullThenException() {
        this.builder.setCandlestickPrice(null);
    }

    @Test
    public void WhenSetAppliedPriceCorrectValueThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {
        CandlestickPrice expected = CandlestickPrice.MEDIAN;
        this.builder.setCandlestickPrice(expected);

        Field field = this.builder.getClass().getDeclaredField("candlestickPrice");
        field.setAccessible(true);
        CandlestickPrice result = (CandlestickPrice) field.get(this.builder);

        assertSame(expected, result);
    }

    @Test
    public void WhenSetAppliedPriceThenReturnSameObject(){
        MABuilder builderObj = this.builder.setCandlestickPrice(CandlestickPrice.CLOSE);

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

    private long getMinPeriod() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("MIN_PERIOD");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

    private long getMaxPeriod() throws NoSuchFieldException, IllegalAccessException {
        Field field = this.builder.getClass().getDeclaredField("MAX_PERIOD");
        field.setAccessible(true);
        return (long) field.get(this.builder);
    }

//    private void setMockRequest() throws NoSuchFieldException, IllegalAccessException {
//        Field request = this.builder.getClass().getDeclaredField("request");
//        request.setAccessible(true);
//        request.set(this.builder, this.mockRequest);
//    }
}