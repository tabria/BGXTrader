package trader.indicators.rsi;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.*;
import org.junit.Before;
import org.junit.Test;
import trader.indicators.enums.CandlestickPrice;
import trader.indicators.Indicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RSIBuilderTest {

    private RSIBuilder builder;
    private Context context;
    private InstrumentCandlesResponse response;
    private List<Candlestick> candlestickList;

    @Before
    public void before() throws Exception {

        this.candlestickList = new ArrayList<>();
        this.response = mock(InstrumentCandlesResponse.class);
        when(this.response.getCandles()).thenReturn(this.candlestickList);
        this.context = mock(Context.class);
        this.context.instrument = mock(InstrumentContext.class);
        when(this.context.instrument.candles(any(InstrumentCandlesRequest.class))).thenReturn(this.response);

        this.builder = new RSIBuilder(context);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateRSIBuilderWithNullContextThenException(){
        new RSIBuilder(null);
    }

    @Test
    public void WhenCreateNewBuilderThenPeriodValueIsDefaultValue() throws NoSuchFieldException, IllegalAccessException {

        Field field = getFieldValue(this.builder, "DEFAULT_PERIOD");
        long expectedPeriod =(long) field.get(this.builder);

        RSIBuilder rsiBuilder = new RSIBuilder(this.context);

        Field field2 = getFieldValue(rsiBuilder, "period");
        long resultPeriod =(long) field.get(this.builder);

        assertEquals(expectedPeriod, resultPeriod);
    }

    @Test
    public void WhenCreateNewBuilderThenAppliedPriceMustBeDefaultValue() throws NoSuchFieldException, IllegalAccessException {
        RSIBuilder rsiBuilder = new RSIBuilder(this.context);

        Field field = getFieldValue(rsiBuilder, "DEFAULT_APPLIED_PRICE");
        CandlestickPrice expected = (CandlestickPrice) field.get(rsiBuilder);

        Field field2 = getFieldValue(rsiBuilder, "candlestickPrice");
        CandlestickPrice candlestickPrice = (CandlestickPrice) field2.get(rsiBuilder);

        assertEquals(expected, candlestickPrice);
    }

    @Test
    public void WhenCreateNewBuilderThenCandlesTimeFrameMustBeDefaultValue() throws NoSuchFieldException, IllegalAccessException {
        RSIBuilder rsiBuilder = new RSIBuilder(this.context);

        Field field = getFieldValue(rsiBuilder,"DEFAULT_CANDLE_TIME_FRAME");
        CandlestickGranularity expected = (CandlestickGranularity) field.get(rsiBuilder);

        Field field2 = getFieldValue(rsiBuilder,"candlesTimeFrame");
        CandlestickGranularity result = (CandlestickGranularity) field2.get(rsiBuilder);

        assertEquals(expected, result);
    }

    @Test
    public void WhenCallSetPeriodThenReturnCurrentObject(){
        RSIBuilder rsiBuilder = this.builder.setPeriod(17L);
        assertEquals(this.builder, rsiBuilder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCallSetPeriodWithLessThanMINValueThenException() throws NoSuchFieldException, IllegalAccessException {

        Field field = getFieldValue(this.builder, "MIN_PERIOD");
        long minValue =(long) field.get(this.builder) ;
        this.builder.setPeriod(minValue - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenSetPeriodWithMoreThanMaxPeriodThenException() throws NoSuchFieldException, IllegalAccessException {

        Field field = getFieldValue(this.builder, "MAX_PERIOD");
        long maxValue =(long) field.get(this.builder);

                this.builder.setPeriod(maxValue + 1);
    }

    @Test
    public void WhenSetPeriodWithCorrectValueThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {

        this.builder.setPeriod(17L);
        Field field =  getFieldValue(this.builder, "period");
        long period = (long) field.get(this.builder);

        assertEquals(17L, period);

    }

    @Test
    public void WhenSetAppliedPriceThenReturnCurrentObject(){
        RSIBuilder rsiBuilder = this.builder.setCandlestickPrice(CandlestickPrice.CLOSE);
        assertEquals(this.builder, rsiBuilder);
    }

    @Test(expected = NullPointerException.class)
    public void WhenSetAppliedPriceWithNullThenException(){
        this.builder.setCandlestickPrice(null);
    }

    @Test
    public void WhenSetAppliedPriceWithCorrectValueThenReturnCorrectValue() throws NoSuchFieldException, IllegalAccessException {

        CandlestickPrice expected = CandlestickPrice.MEDIAN;
        this.builder.setCandlestickPrice(expected);

        Field field = getFieldValue(this.builder,"candlestickPrice");
        CandlestickPrice result = (CandlestickPrice) field.get(this.builder);

        assertEquals(expected, result);
    }

    @Test
    public void WhenSetCandlesTimeFrameThenReturnSameObject(){
        RSIBuilder builder = this.builder.setCandlesTimeFrame(CandlestickGranularity.M1);
        assertEquals(this.builder, builder);
    }

    @Test(expected = NullPointerException.class)
    public void WhenSetCandlesTimeFrameWithNullThenException(){
        this.builder.setCandlesTimeFrame(null);
    }

    @Test
    public void WhenSetCandlesTimeFrameWithCorrectValueThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {

        CandlestickGranularity expected = CandlestickGranularity.D;
        this.builder.setCandlesTimeFrame(expected);

        Field field = getFieldValue(this.builder, "candlesTimeFrame");
        CandlestickGranularity result = (CandlestickGranularity) field.get(this.builder);

        assertEquals(expected, result);
    }

    @Test
    public void WhenBuildThenReturnCorrectObject(){
        Indicator rsi = this.builder.build();
        String rsiName = rsi.getClass().getSimpleName();
        assertEquals("The object is not RSI","RelativeStrengthIndex", rsiName);

    }

    private Field getFieldValue(RSIBuilder currentBuilder, String fieldName) throws NoSuchFieldException{
        Field field = currentBuilder.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}