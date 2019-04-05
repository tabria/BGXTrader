package trader.price;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static trader.price.PriceImpl.*;

public class PriceImplBuilderTest {

    private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal NEGATIVE_PRICE = new BigDecimal(-1.234)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal POSITIVE_PRICE = new BigDecimal(4.567889)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

    PriceBuilder priceBuilder;
    CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {
        priceBuilder = new PriceBuilder();
        commonMembers = new CommonTestClassMembers();
    }

    @Test
    public void buildMustReturnPriceObject(){
        String actual = priceBuilder.build().getClass().getSimpleName();
        assertEquals("PriceImpl", actual);
    }

    @Test
    public void createBuilderWithDefaultAsk(){
        BigDecimal ask = (BigDecimal) commonMembers.extractFieldObject(priceBuilder, "ask");
        assertEquals(0, ask.compareTo(DEFAULT_ASK));
    }

    @Test
    public void createBuilderWithDefaultBid(){
        BigDecimal bid = (BigDecimal) commonMembers.extractFieldObject(priceBuilder, "bid");
        assertEquals(0, bid.compareTo(DEFAULT_BID));
    }

    @Test
    public void createBuilderWithDefaultZonedDateTime(){
        ZonedDateTime dateTime = (ZonedDateTime) commonMembers.extractFieldObject(priceBuilder, "dateTime");
        assertEquals(0, dateTime.compareTo(DEFAULT_DATE_TIME));
    }

    @Test
    public void createBuilderWithDefaultTradableFlag(){
        boolean tradable = (boolean) commonMembers.extractFieldObject(priceBuilder, "isTradable");
        assertTrue(tradable);
    }

    @Test
    public void createBuilderWithDefaultAvailableUnits(){
        BigDecimal availableUnits = (BigDecimal) commonMembers.extractFieldObject(priceBuilder, "availableUnits");
        assertEquals(0, availableUnits.compareTo(BigDecimal.ZERO));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetAskWithNull_Exception(){
        priceBuilder.setAsk(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetAskWithNegativeNumber_Exception(){
        priceBuilder.setAsk(NEGATIVE_PRICE);
    }

    @Test
    public void callAsk(){
        priceBuilder.setAsk(POSITIVE_PRICE);
        BigDecimal priceAsk = (BigDecimal) commonMembers.extractFieldObject(priceBuilder,"ask");

        assertEquals(0, priceAsk.compareTo(POSITIVE_PRICE));
    }

    @Test
    public void WhenCallSetAsk_ReturnCurrentObject(){
        assertEquals(priceBuilder, priceBuilder.setAsk(POSITIVE_PRICE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetBidWithNull_Exception(){
        priceBuilder.setBid(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetBidWithNegativeNumber_Exception(){
        priceBuilder.setBid(NEGATIVE_PRICE);
    }

    @Test
    public void callBid(){
        priceBuilder.setBid(POSITIVE_PRICE);
        BigDecimal priceBid = (BigDecimal) commonMembers.extractFieldObject(priceBuilder,"bid");

        assertEquals(0, priceBid.compareTo(POSITIVE_PRICE));
    }

    @Test
    public void WhenCallSetBid_ReturnCurrentObject(){
        assertEquals(priceBuilder, priceBuilder.setBid(POSITIVE_PRICE));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetDateTimeWithNull_Exception(){
        priceBuilder.setDateTime(null);
    }

    @Test
    public void callSetDateTime(){
        ZonedDateTime expected = ZonedDateTime.now();
        priceBuilder.setDateTime(expected);
        ZonedDateTime actual = (ZonedDateTime) commonMembers.extractFieldObject(priceBuilder,"dateTime");

        assertEquals(0, actual.compareTo(expected) );
    }

    @Test
    public void WhenCallSetDateTime_ReturnCurrentObject(){
        ZonedDateTime expected = ZonedDateTime.now();
        assertEquals(priceBuilder, priceBuilder.setDateTime(expected));
    }

    @Test
    public void callSetIsTradable(){
        priceBuilder.setIsTradable(false);
        boolean actual = (boolean) commonMembers.extractFieldObject(priceBuilder,"isTradable");

        assertFalse(actual);
    }

    @Test
    public void WhenCallSetIsTradable_ReturnCurrentObject(){
        assertEquals(priceBuilder, priceBuilder.setIsTradable(false));
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetAvailableUnitsWithNegativeNumber_Exception(){
        priceBuilder.setAvailableUnits(NEGATIVE_PRICE);
    }

    @Test
    public void callAvailableUnits(){
        priceBuilder.setAvailableUnits(POSITIVE_PRICE);
        BigDecimal availableUnits = (BigDecimal) commonMembers.extractFieldObject(priceBuilder,"availableUnits");

        assertEquals(availableUnits, POSITIVE_PRICE);
    }

    @Test
    public void WhenCallSetAvailableUnits_ReturnCurrentObject(){
        assertEquals(priceBuilder, priceBuilder.setAvailableUnits(POSITIVE_PRICE));
    }


}
