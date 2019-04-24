package trader.entity.trade;

import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.point.PointImpl;
import trader.exception.NegativeNumberException;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TradeImplTest {


    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(0.0001);

    private Trade trade;
    private PointImpl mockPoint;

    @Before
    public void before(){

        trade = new TradeImpl();
        this.mockPoint = mock(PointImpl.class);

    }

    @Test
    public void WhenCreateThenDefaultValues(){
        assertFalse(trade.getTradable());
        assertEquals(Direction.FLAT, trade.getDirection());
        assertEquals(DEFAULT_PRICE, trade.getEntryPrice());
        assertEquals(DEFAULT_PRICE, trade.getStopLossPrice());
    }

    @Test
    public void WhenTryToSetFieldsToNull_Default(){
        setWithNull(null);

        assertDefaultValues();
    }

    @Test
    public void WhenTryToSetFieldsWithEmptyValue_Default(){
        setWithNull("   ");

        assertDefaultValues();
    }


    @Test
    public void WhenTryToSetDirectionWithCorrectValue_CorrectUpdate(){
        trade.setDirection("up");

        assertEquals(Direction.UP, trade.getDirection());
    }

    @Test
    public void WhenTryToSetTradableWithCorrectValue_CorrectUpdate(){
        trade.setTradable("true");

        assertTrue(trade.getTradable());
    }

    @Test
    public void WhenTryToSetEntryPriceWithCorrectValue_CorrectUpdate(){
        trade.setEntryPrice("12");

        assertEquals(BigDecimal.valueOf(12), trade.getEntryPrice());
    }

    @Test
    public void WhenTryToSetStopLossPriceWithCorrectValue_CorrectUpdate(){
        trade.setStopLossPrice("0.045");

        assertEquals(BigDecimal.valueOf(0.045), trade.getStopLossPrice());
    }


    @Test(expected = IllegalArgumentException.class)
    public void WhenTryToSetDirectionWithNotExistingDirection_Exception(){
        trade.setDirection("bop");

        assertEquals(Direction.UP, trade.getDirection());
    }

    @Test
    public void WhenTryToSetTradableWithNotExistingValue_Default(){
        trade.setTradable("rrrr");
    }

    @Test(expected = NumberFormatException.class)
    public void WhenTryToSetEntryPriceWithNotExistingValue_Exception(){
        trade.setEntryPrice("rrrr");
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenTryToSetEntryPriceWithValueLessThan0_Exception(){
        trade.setEntryPrice("-0.002");
    }

    @Test(expected = NumberFormatException.class)
    public void WhenTryToSetStopPriceWithNotExistingValue_Exception(){
        trade.setEntryPrice("rrrr");
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenTryToSetStopLossPriceWithValueLessThan0_Exception(){
        trade.setEntryPrice("-0.1222");
    }

    @Test
    public void WhenCallToStringThenCorrectResult(){
        trade.setDirection(Direction.FLAT.toString());
        trade.setStopLossPrice(DEFAULT_PRICE.toString());
        trade.setEntryPrice(DEFAULT_PRICE.toString());
        trade.setTradable("false");
        String expected = String.format("[TRADE] direction@%s, entry@%s, SL@%s, units@", "FLAT", DEFAULT_PRICE, DEFAULT_PRICE);

        assertEquals(expected, trade.toString());
    }

    private void setWithNull(String o) {
        trade.setDirection(o);
        trade.setTradable(o);
        trade.setEntryPrice(o);
        trade.setStopLossPrice(o);
    }

    private void assertDefaultValues() {
        assertEquals(Direction.FLAT, trade.getDirection());
        assertFalse(trade.getTradable());
        assertEquals(DEFAULT_PRICE ,trade.getEntryPrice());
        assertEquals(DEFAULT_PRICE ,trade.getStopLossPrice());
    }
}