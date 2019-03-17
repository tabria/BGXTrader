package trader.prices;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class PriceTest {

    private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
            .setScale(5, RoundingMode.HALF_UP);
    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

    @Test
    public void createPriceWithDefaultValues(){
        Price price = new Price.PriceBuilder().build();

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

}
