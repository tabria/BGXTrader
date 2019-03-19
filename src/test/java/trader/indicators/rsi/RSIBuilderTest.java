package trader.indicators.rsi;

import com.oanda.v20.instrument.*;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPIMock.OandaAPIMock;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.candle.CandleGranularity;
import trader.candle.CandlestickPriceType;
import trader.indicators.Indicator;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RSIBuilderTest {

    private static final String DEFAULT_CANDLESTICK_QUANTITY_FIELD_NAME = "DEFAULT_CANDLESTICKS_QUANTITY";
    private static final String CANDLESTICKS_QUANTITY_FIELD_NAME = "candlesticksQuantity";
    private static final String DEFAULT_CANDLESTICK_PRICE_TYPE_FIELD_NAME = "DEFAULT_CANDLESTICK_PRICE_TYPE";
    private static final String CANDLESTICK_PRICE_TYPE_FIELD_NAME = "candlestickPriceType";
    private static final String DEFAULT_CANDLE_GRANULARITY_FIELD_NAME = "DEFAULT_CANDLE_GRANULARITY";
    private static final String CANDLE_GRANULARITY_FIELD_NAME = "candleGranularity";
    private static final String MIN_CANDLESTICKS_QUANTITY_FIELD_NAME = "MIN_CANDLESTICKS_QUANTITY";
    private static final String MAX_CANDLESTICKS_QUANTITY_FIELD_NAME = "MAX_CANDLESTICKS_QUANTITY";
    private static final long NEW_CANDLESTICKS_QUANTITY = 17L;

    private RSIBuilder builder;
    @Before
    public void before() throws Exception {
        OandaAPIMockInstrument oandaInstrument = new OandaAPIMockInstrument();
        when(oandaInstrument.getMockResponse().getCandles()).thenReturn(new ArrayList<>());
        when(oandaInstrument.getContext().instrument.candles(any(InstrumentCandlesRequest.class)))
                .thenReturn(oandaInstrument.getMockResponse());

        builder = new RSIBuilder(oandaInstrument.getContext());

    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateRSIBuilderWithNullContext_Exception(){
        new RSIBuilder(null);
    }

    @Test
    public void testCreateNewBuilderWithDefaultCandlesticksQuantity() throws NoSuchFieldException, IllegalAccessException {
        Field defaultCandlesticksQuantityField = getFieldValue(builder, DEFAULT_CANDLESTICK_QUANTITY_FIELD_NAME);
        long expected =(long) defaultCandlesticksQuantityField.get(builder);
        Field candlesticksQuantityField = getFieldValue(builder, CANDLESTICKS_QUANTITY_FIELD_NAME);
        long actual =(long) candlesticksQuantityField.get(builder);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateNewBuilderWithDefaultCandlestickPriceType() throws NoSuchFieldException, IllegalAccessException {
        Field defaultCandlestickPriceTypeField = getFieldValue(builder, DEFAULT_CANDLESTICK_PRICE_TYPE_FIELD_NAME);
        CandlestickPriceType expected = (CandlestickPriceType) defaultCandlestickPriceTypeField.get(builder);
        Field candlestickPriceTypeField = getFieldValue(builder, CANDLESTICK_PRICE_TYPE_FIELD_NAME);
        CandlestickPriceType actual = (CandlestickPriceType) candlestickPriceTypeField.get(builder);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateNewBuilderWithDefaultCandleGranularity() throws NoSuchFieldException, IllegalAccessException {
        Field defaultCandleGranularityField = getFieldValue(builder, DEFAULT_CANDLE_GRANULARITY_FIELD_NAME);
        CandleGranularity expected = (CandleGranularity) defaultCandleGranularityField.get(builder);
        Field candleGranularityField = getFieldValue(builder, CANDLE_GRANULARITY_FIELD_NAME);
        CandleGranularity actual = ( CandleGranularity) candleGranularityField.get(builder);

        assertEquals(expected, actual);
    }

    @Test
    public void WhenCallSetCandlesticksQuantity_ReturnCurrentObject(){
        assertEquals(builder, builder.setCandlesticksQuantity(NEW_CANDLESTICKS_QUANTITY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCallSetCandlesticksQuantityWithLessThanMINCandlesticksQuantity_Exception() throws NoSuchFieldException, IllegalAccessException {
        Field minCandlesticksQuantityField = getFieldValue(builder, MIN_CANDLESTICKS_QUANTITY_FIELD_NAME);
        long expected =(long) minCandlesticksQuantityField.get(this.builder) ;
        builder.setCandlesticksQuantity(expected - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCallSetCandlesticksQuantityWithMoreThanMAXCandlesticksQuantity_Exception() throws NoSuchFieldException, IllegalAccessException {
        Field maxCandlesticksQuantityField = getFieldValue(builder, MAX_CANDLESTICKS_QUANTITY_FIELD_NAME);
        long expected =(long) maxCandlesticksQuantityField.get(builder);
        builder.setCandlesticksQuantity(expected + 1);
    }

    @Test
    public void callSetCandlesticksQuantityWithCorrectValue_SuccessfulUpdate() throws NoSuchFieldException, IllegalAccessException {
        builder.setCandlesticksQuantity(NEW_CANDLESTICKS_QUANTITY);
        Field candlestickQuantityField =  getFieldValue(builder, CANDLESTICKS_QUANTITY_FIELD_NAME);
        long actual = (long) candlestickQuantityField.get(builder);

        assertEquals(NEW_CANDLESTICKS_QUANTITY, actual);
    }

    @Test
    public void WhenCallSetCandlestickPriceType_ReturnCurrentObject(){
        assertEquals(builder, builder.setCandlestickPriceType(CandlestickPriceType.CLOSE));
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlestickPriceTypeWithNull_Exception(){
        builder.setCandlestickPriceType(null);
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectValue_SuccessfulUpdate() throws NoSuchFieldException, IllegalAccessException {
        CandlestickPriceType expected = CandlestickPriceType.MEDIAN;
        builder.setCandlestickPriceType(expected);
        Field candlestickPriceTypeField = getFieldValue(builder,CANDLESTICK_PRICE_TYPE_FIELD_NAME);
        CandlestickPriceType actual = (CandlestickPriceType) candlestickPriceTypeField.get(this.builder);

        assertEquals(expected, actual);
    }

    @Test
    public void WhenCallSetCandleGranularity_ReturnSameObject(){
        RSIBuilder rsiBuilder = builder.setCandleGranularity(CandleGranularity.M1);
        assertEquals(builder, rsiBuilder);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandleGranularityWithNull_Exception(){
        builder.setCandleGranularity(null);
    }

    @Test
    public void WhenCallSetCandleGranularityWithCorrectValue_SuccessfulUpdate() throws NoSuchFieldException, IllegalAccessException {
        CandleGranularity expected = CandleGranularity.D;
        builder.setCandleGranularity(expected);
        Field candleGranularityField = getFieldValue(builder, CANDLE_GRANULARITY_FIELD_NAME);
        CandleGranularity actual = ( CandleGranularity) candleGranularityField.get(this.builder);

        assertEquals(expected, actual);
    }

    @Test
    public void WhenCallBuild_SuccessfulBuild(){
        Indicator rsi = builder.build();
        String rsiName = rsi.getClass().getSimpleName();

        assertEquals("The object is not RSI Indicator","RelativeStrengthIndex", rsiName);
    }

    private Field getFieldValue(RSIBuilder currentBuilder, String fieldName) throws NoSuchFieldException{
        Field field = currentBuilder.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}