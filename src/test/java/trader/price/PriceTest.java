package trader.price;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Objects;

import static org.junit.Assert.*;

public class PriceTest {

    private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
            .setScale(5, RoundingMode.HALF_UP);
    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

    private Price price;

    @Before
    public void setUp() throws Exception {
        price = new Price.PriceBuilder().build();
    }

    @Test
    public void createPriceWithDefaultValues(){

        assertEquals(price.getAsk(), DEFAULT_ASK);
        assertEquals(price.getBid(), DEFAULT_BID);
        assertEquals(price.getDateTime(), DEFAULT_DATE_TIME);
        assertTrue(price.isTradable());

    }

    @Test
    public void createPriceNoDefaults(){
        BigDecimal askBidPrice = new BigDecimal(1.1).setScale(5, RoundingMode.HALF_UP);
        ZonedDateTime currentTime = ZonedDateTime.now();
        Price price = new Price.PriceBuilder()
                .setAsk(askBidPrice)
                .setBid(askBidPrice)
                .setDateTime(currentTime)
                .setIsTradable(false)
                .build();

        assertEquals(askBidPrice, price.getAsk());
        assertEquals(askBidPrice, price.getBid());
        assertEquals(currentTime, price.getDateTime());
        assertFalse(price.isTradable());
    }

    @Test
    public void testEqualsWithSameObject(){
        assertTrue(price.equals(price));
    }

    @Test
    public void testEqualsWithNull(){
        assertFalse(price.equals(null));
    }

    @Test
    public void testEqualsWithEqualObject(){
        Price equalPrice = new Price.PriceBuilder().build();
        assertTrue(price.equals(equalPrice));
    }

    @Test
    public void testEqualsWithNotEqualAskPrice(){
        Price notEqualPrice = new Price.PriceBuilder()
                .setAsk(BigDecimal.TEN)
                .build();
        assertFalse(price.equals(notEqualPrice));
    }

    @Test
    public void testEqualsWithNotEqualBidPrice(){
        Price notEqualPrice = new Price.PriceBuilder()
                .setBid(BigDecimal.TEN)
                .build();
        assertFalse(price.equals(notEqualPrice));
    }

    @Test
    public void testEqualsWithNotEqualTradable(){
        Price notEqualPrice = new Price.PriceBuilder()
                .setIsTradable(false)
                .build();
        assertFalse(price.equals(notEqualPrice));
    }

    @Test
    public void testEqualsWithNotEqualDateTime(){
        Price notEqualPrice = new Price.PriceBuilder()
                .setDateTime(ZonedDateTime.now())
                .build();
        assertFalse(price.equals(notEqualPrice));
    }

    @Test
    public void testEqualsWithNotEqualUnits(){
        Price notEqualPrice = new Price.PriceBuilder()
                .setAvailableUnits(BigDecimal.TEN)
                .build();
        assertFalse(price.equals(notEqualPrice));
    }

    @Test
    public void testEqualsWithFullyNotEqualObject(){
        Price notEqualPrice = new Price.PriceBuilder()
                .setAsk(BigDecimal.TEN)
                .setBid(BigDecimal.TEN)
                .setIsTradable(false)
                .setDateTime(ZonedDateTime.now())
                .setAvailableUnits(BigDecimal.TEN)
                .build();
        assertFalse(price.equals(notEqualPrice));
    }

    @Test
    public void testHashCode(){
        int expected = Objects.hash(price.getAsk(), price.getBid(), price.getDateTime(), price.isTradable(), price.getAvailableUnits());

        assertTrue(expected == price.hashCode());
    }

    @Test
    public void testToString(){
        String expected = "Price{ask=0.01000, bid=0.02000, dateTime=2012-06-30T12:30:40Z[UTC], isTradable=true, availableUnits=0}";

        assertEquals(expected, price.toString());
    }

}
