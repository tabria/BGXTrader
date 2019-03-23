package trader.candlestick.candle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CandleTest {

    private static final long DEFAULT_VOLUME = 1L;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final ZonedDateTime DEFAULT_ZONED_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

    private Candle candle;

    @Before
    public void setUp() throws Exception {
        candle = new Candle.CandleBuilder().build();
    }

    @Test
    public void testGetCandlePrices(){

        ZonedDateTime dateTime = candle.getDateTime();
        assertEquals(DEFAULT_PRICE, candle.getOpenPrice());
        assertEquals(DEFAULT_PRICE, candle.getClosePrice());
        assertEquals(DEFAULT_PRICE, candle.getLowPrice());
        assertEquals(DEFAULT_PRICE, candle.getHighPrice());
        assertEquals(0, DEFAULT_ZONED_DATE_TIME.compareTo(dateTime));
        assertEquals(DEFAULT_VOLUME,candle.getVolume());
        assertTrue(candle.isComplete());
    }
}
