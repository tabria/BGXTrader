package trader.candles;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.candles.Candle.*;

public class CandleBuilderTest {


    private static final String PRICE_TYPE = "LOW";
    private static final long MIN_TIME_FRAME_IN_SECONDS = 5L;
    private static final long MAX_TIME_FRAME_IN_SECONDS = 2629800L;
    private static final long TIME_FRAME_IN_SECONDS = 60L;
    private static final long VOLUME = 10L;
    private static final BigDecimal NEGATIVE_PRICE = new BigDecimal(-1.234).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal POSITIVE_PRICE = new BigDecimal(4.567889).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final ZonedDateTime DEFAULT_ZONED_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

    CandleBuilder candleBuilder;
    CandlePriceType closeCandlePriceType;
    Candle candle;

    @Before
    public void setUp() throws Exception {
        candle = new CandleBuilder().build();
        candleBuilder = new CandleBuilder();
        closeCandlePriceType = mock(CloseCandlePriceType.class);

    }

    @Test
    public void WhenCallBuild_SuccessfulBuild(){
        Candle candle = candleBuilder.build();
        String candleName = candle.getClass().getSimpleName();

        assertEquals("Candle", candleName);
    }

    @Test(expected = NullArgumentException.class)
    public void callSetCandlePriceTypeWithNull_Exception(){
        candleBuilder.setPriceType(null);
    }

    @Test
    public void setCorrectPriceType(){
        candleBuilder.setPriceType(closeCandlePriceType);
        CandlePriceType candlePriceType = (CandlePriceType) extractFieldObject("priceType");
        when(closeCandlePriceType.getType()).thenReturn(PRICE_TYPE);
        assertEquals(candlePriceType.getType(), PRICE_TYPE);
    }

    @Test
    public void WhenCallSetCandlePriceType_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setPriceType(closeCandlePriceType));
    }

    @Test(expected = UnderflowException.class)
    public void callSetTimeFrameWithLessThanMinimumSeconds_Exception(){
        candleBuilder.setTimeFrame(MIN_TIME_FRAME_IN_SECONDS - 1);
    }

    @Test(expected = OverflowException.class)
    public void callSetTimeFrameWithMoreThanMaxSeconds_Exception(){
        candleBuilder.setTimeFrame(MAX_TIME_FRAME_IN_SECONDS + 1);
    }

    @Test
    public void setTimeFrame(){
        candleBuilder.setTimeFrame(TIME_FRAME_IN_SECONDS);
        long actual = (long) extractFieldObject("timeFrame");

        assertEquals(TIME_FRAME_IN_SECONDS, actual);
    }

    @Test
    public void WhenCallCandleTimeFrame_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setTimeFrame(TIME_FRAME_IN_SECONDS));
    }

    @Test
    public void callSetComplete(){
        candleBuilder.setComplete(false);
        boolean actual = (boolean) extractFieldObject("complete");

        assertFalse(actual);
    }

    @Test
    public void WhenCallSetComplete_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setComplete(false));
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetVolumeWithNegativeValue_Exception(){
        candleBuilder.setVolume(-1L);
    }

    @Test
    public void callSetVolume(){
        candleBuilder.setVolume(VOLUME);
        long actual = (long) extractFieldObject("volume");

        assertEquals(actual, VOLUME);
    }

    @Test
    public void WhenCallSetVolume_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setVolume(VOLUME));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetOpenPriceWithNull_Exception(){
        candleBuilder.setOpenPrice(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetOpenPriceWithNegativeNumber_Exception(){
        candleBuilder.setOpenPrice(NEGATIVE_PRICE);
    }

    @Test
    public void callSetOpenPrice(){
        candleBuilder.setOpenPrice(POSITIVE_PRICE);
        BigDecimal candleOpenPrice = (BigDecimal) extractFieldObject("openPrice");

        assertEquals(0, candleOpenPrice.compareTo(POSITIVE_PRICE));
    }

    @Test
    public void WhenCallSetOpenPrice_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setOpenPrice(POSITIVE_PRICE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetHighPriceWithNull_Exception(){
        candleBuilder.setHighPrice(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetHighPriceWithNegativeNumber_Exception(){
        candleBuilder.setHighPrice(NEGATIVE_PRICE);
    }

    @Test
    public void callSetHighPrice(){
        candleBuilder.setHighPrice(POSITIVE_PRICE);
        BigDecimal candleHighPrice = (BigDecimal) extractFieldObject("highPrice");

        assertEquals(0, candleHighPrice.compareTo(POSITIVE_PRICE));
    }

    @Test
    public void WhenCallSetHighPrice_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setHighPrice(POSITIVE_PRICE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetLowPriceWithNull_Exception(){
        candleBuilder.setLowPrice(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetLowPriceWithNegativeNumber_Exception(){
        candleBuilder.setLowPrice(NEGATIVE_PRICE);
    }

    @Test
    public void callSetLowPrice(){
        candleBuilder.setLowPrice(POSITIVE_PRICE);
        BigDecimal candleLowPrice = (BigDecimal) extractFieldObject("lowPrice");

        assertEquals(0, candleLowPrice.compareTo(POSITIVE_PRICE));
    }

    @Test
    public void WhenCallSetLowPrice_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setLowPrice(POSITIVE_PRICE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetClosePriceWithNull_Exception(){
        candleBuilder.setClosePrice(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetClosePriceWithNegativeNumber_Exception(){
        candleBuilder.setClosePrice(NEGATIVE_PRICE);
    }

    @Test
    public void callSetClosePrice(){
        candleBuilder.setClosePrice(POSITIVE_PRICE);
        BigDecimal candleClosePrice = (BigDecimal) extractFieldObject("closePrice");

        assertEquals(0, candleClosePrice.compareTo(POSITIVE_PRICE));
    }

    @Test
    public void WhenCallSetClosePrice_ReturnCurrentObject(){
        assertEquals(candleBuilder, candleBuilder.setClosePrice(POSITIVE_PRICE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetDateTimeWithNull_Exception(){
        candleBuilder.setDateTime(null);
    }

    @Test
    public void callSetDateTime(){
        ZonedDateTime expected = ZonedDateTime.now();
        candleBuilder.setDateTime(expected);
        ZonedDateTime actual = (ZonedDateTime) extractFieldObject("dateTime");

        assertEquals(0, actual.compareTo(expected) );
    }

    @Test
    public void WhenCallSetDateTime_ReturnCurrentObject(){
        ZonedDateTime expected = ZonedDateTime.now();
        assertEquals(candleBuilder, candleBuilder.setDateTime(expected));
    }

    @Test
    public void createCandleBuilderWitPriceType_Default(){
        when(closeCandlePriceType.getType()).thenReturn("CLOSE");
        String expected = candle.getPriceType().getType();
        assertEquals("CLOSE", expected);
    }

    @Test
    public void createCandleBuilderWithCandleTimeFrame_Default(){
        assertEquals(1_800L, candle.getTimeFrame());
    }

    @Test
    public void createCandleBuilderWithComplete_Default(){
        assertTrue(candle.isComplete());
    }

    @Test
    public void createCandleBuilderWithVolume_Default(){
        assertEquals(1, candle.getVolume());
    }

    @Test
    public void createCandleBuilderWithOpenPrice_Default(){
        assertEquals(0, DEFAULT_PRICE.compareTo(candle.getOpenPrice()));
    }

    @Test
    public void createCandleBuilderWithHighPrice_Default(){
        assertEquals(0, DEFAULT_PRICE.compareTo(candle.getHighPrice()));
    }

    @Test
    public void createCandleBuilderWithLowPrice_Default(){
        assertEquals(0, DEFAULT_PRICE.compareTo(candle.getLowPrice()));
    }

    @Test
    public void createCandleBuilderWithClosePrice_Default(){
        assertEquals(0, DEFAULT_PRICE.compareTo(candle.getClosePrice()));
    }

    @Test
    public void createCandleBuilderWithDateTime_Default(){
        assertEquals(0, DEFAULT_ZONED_DATE_TIME.compareTo(candle.getDateTime()));
    }

    private Object extractFieldObject(String fieldName) {
        try {
            Field field = candleBuilder.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(candleBuilder);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
