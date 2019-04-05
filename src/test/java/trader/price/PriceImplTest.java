package trader.price;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Objects;

import static org.junit.Assert.*;

public class PriceImplTest {

    private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
            .setScale(5, RoundingMode.HALF_UP);
    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

    private PriceImpl priceImpl;

    @Before
    public void setUp() {
        priceImpl = new PriceImpl.PriceBuilder().build();
    }

    @Test
    public void createPriceWithDefaultValues(){

        assertEquals(priceImpl.getAsk(), DEFAULT_ASK);
        assertEquals(priceImpl.getBid(), DEFAULT_BID);
        assertEquals(priceImpl.getDateTime(), DEFAULT_DATE_TIME);
        assertTrue(priceImpl.isTradable());

    }

    @Test
    public void createPriceNoDefaults(){
        BigDecimal askBidPrice = new BigDecimal(1.1).setScale(5, RoundingMode.HALF_UP);
        ZonedDateTime currentTime = ZonedDateTime.now();
        PriceImpl priceImpl = new PriceImpl.PriceBuilder()
                .setAsk(askBidPrice)
                .setBid(askBidPrice)
                .setDateTime(currentTime)
                .setIsTradable(false)
                .build();

        assertEquals(askBidPrice, priceImpl.getAsk());
        assertEquals(askBidPrice, priceImpl.getBid());
        assertEquals(currentTime, priceImpl.getDateTime());
        assertFalse(priceImpl.isTradable());
    }

    @Test
    public void testEqualsWithSameObject(){
        assertEquals(priceImpl, priceImpl);
    }

    @Test
    public void testEqualsWithNull(){
        assertFalse(priceImpl.equals(null));
    }

    @Test
    public void testEqualsWithEqualObject(){
        PriceImpl equalPriceImpl = new PriceImpl.PriceBuilder().build();
        assertEquals(priceImpl, equalPriceImpl);
    }

    @Test
    public void testEqualsWithNotEqualAskPrice(){
        PriceImpl notEqualPriceImpl = new PriceImpl.PriceBuilder()
                .setAsk(BigDecimal.TEN)
                .build();
        assertNotEquals(priceImpl, notEqualPriceImpl);
    }

    @Test
    public void testEqualsWithNotEqualBidPrice(){
        PriceImpl notEqualPriceImpl = new PriceImpl.PriceBuilder()
                .setBid(BigDecimal.TEN)
                .build();
        assertNotEquals(priceImpl, notEqualPriceImpl);
    }

    @Test
    public void testEqualsWithNotEqualTradable(){
        PriceImpl notEqualPriceImpl = new PriceImpl.PriceBuilder()
                .setIsTradable(false)
                .build();
        assertNotEquals(priceImpl, notEqualPriceImpl);
    }

    @Test
    public void testEqualsWithNotEqualDateTime(){
        PriceImpl notEqualPriceImpl = new PriceImpl.PriceBuilder()
                .setDateTime(ZonedDateTime.now())
                .build();
        assertNotEquals(priceImpl, notEqualPriceImpl);
    }

    @Test
    public void testEqualsWithNotEqualUnits(){
        PriceImpl notEqualPriceImpl = new PriceImpl.PriceBuilder()
                .setAvailableUnits(BigDecimal.TEN)
                .build();
        assertNotEquals(priceImpl, notEqualPriceImpl);
    }

    @Test
    public void testEqualsWithFullyNotEqualObject(){
        PriceImpl notEqualPriceImpl = new PriceImpl.PriceBuilder()
                .setAsk(BigDecimal.TEN)
                .setBid(BigDecimal.TEN)
                .setIsTradable(false)
                .setDateTime(ZonedDateTime.now())
                .setAvailableUnits(BigDecimal.TEN)
                .build();
        assertNotEquals(priceImpl, notEqualPriceImpl);
    }

    @Test
    public void testHashCode(){
        int expected = Objects.hash(priceImpl.getAsk(), priceImpl.getBid(), priceImpl.getDateTime(), priceImpl.isTradable(), priceImpl.getAvailableUnits());

        assertEquals(expected, priceImpl.hashCode());
    }

    @Test
    public void testToString(){
        String expected = "PriceImpl{ask=0.01000, bid=0.02000, dateTime=2012-06-30T12:30:40Z[UTC], isTradable=true, availableUnits=0}";

        assertEquals(expected, priceImpl.toString());
    }

}
