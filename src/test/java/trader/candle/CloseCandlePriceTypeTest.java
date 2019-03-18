package trader.candle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CloseCandlePriceTypeTest {

    CloseCandlePriceType closeCandlePriceType;

    @Before
    public void setUp() throws Exception {
        closeCandlePriceType = new CloseCandlePriceType();
    }

    @Test
    public void callGetPriceType(){
        String actual = closeCandlePriceType.getType();

        assertEquals("CLOSE", actual);

    }

    @Test
    public void callToString(){
        String actual = closeCandlePriceType.toString();

        assertEquals("CLOSE", actual);
    }
}
