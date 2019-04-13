package trader.entity.trade;

import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class BrokerTradeDetailsTest {


    private static final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(0.0001);
    private static final String DEFAULT_ID = "0";

    private BrokerTradeDetailsImpl tradeDetails;

    @Before
    public void setUp() throws Exception {

        tradeDetails = new BrokerTradeDetailsImpl();
    }

    @Test
    public void WhenInstantiate_DefaultValues(){

        assertEquals(DEFAULT_ID, tradeDetails.getTradeID());
        assertEquals(DEFAULT_VALUE, tradeDetails.getOpenPrice());
        assertEquals(DEFAULT_VALUE, tradeDetails.getStopLossPrice());
        assertEquals(DEFAULT_VALUE, tradeDetails.getCurrentUnits());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenSetTradeIDWithNull_Exception(){
        tradeDetails.setTradeID(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenSetTradeIDWithEmptyString_Exception(){
        tradeDetails.setTradeID("  ");
    }

    @Test
    public void WhenSetTradeIDWithCorrectStringWithSpaces_CorrectUpdate(){
        tradeDetails.setTradeID(" 12  ");

        assertEquals("12", tradeDetails.getTradeID());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenSetStopLossOrderIDWithNull_Exception(){
        tradeDetails.setStopLossOrderID(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenSetStopLossOrderIDWithEmptyString_Exception(){
        tradeDetails.setStopLossOrderID("  ");
    }

    @Test
    public void WhenSetStopLossOrderIDWithCorrectStringWithSpaces_CorrectUpdate(){
        tradeDetails.setStopLossOrderID(" 12  ");

        assertEquals("12", tradeDetails.getStopLossOrderID());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenSetOpenPriceWithNull_Exception(){
        tradeDetails.setOpenPrice(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenSetOpenPriceWithEmptyString_Exception(){
        tradeDetails.setOpenPrice("  ");
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenSetOpenPriceWithNegativeNumber_Exception(){
        tradeDetails.setOpenPrice("-123");
    }

    @Test
    public void WhenSetOpenPriceWithCorrectStringWithSpaces_CorrectUpdate(){
        tradeDetails.setOpenPrice(" 1.1234  ");

        assertEquals(new BigDecimal("1.1234"), tradeDetails.getOpenPrice());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenSetStopLossPriceWithNull_Exception(){
        tradeDetails.setStopLossPrice(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenSetStopLossPriceWithEmptyString_Exception(){
        tradeDetails.setStopLossPrice("  ");
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenSetStopLossPriceWithNegativeNumber_Exception(){
        tradeDetails.setStopLossPrice("-123");
    }

    @Test
    public void WhenSetStopLossPriceWithCorrectStringWithSpaces_CorrectUpdate(){
        tradeDetails.setStopLossPrice(" 1.1234  ");

        assertEquals(new BigDecimal("1.1234"), tradeDetails.getStopLossPrice());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenSetUnitsWithNull_Exception(){
        tradeDetails.setCurrentUnits(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenSetUnitsWithEmptyString_Exception(){
        tradeDetails.setCurrentUnits("  ");
    }

    @Test
    public void WhenSetUnitsWithCorrectStringWithSpaces_CorrectUpdate(){
        tradeDetails.setCurrentUnits(" -200  ");

        assertEquals(new BigDecimal("-200"), tradeDetails.getCurrentUnits());
    }
}
