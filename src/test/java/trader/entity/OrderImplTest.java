package trader.entity;

import org.junit.Assert;
import org.junit.Test;
import trader.entity.order.OrderImpl;
import trader.entity.order.enums.OrderType;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class OrderImplTest {

    private static final BigDecimal DEFAULT_UNITS_SIZE = BigDecimal.valueOf(123);
    private static final BigDecimal DEFAULT_STOP_LOSS_PRICE = BigDecimal.valueOf(1.2000);

    @Test(expected = NullArgumentException.class)
    public void WhenCreateOrderWithNullID_Exception(){
        new OrderImpl(null, OrderType.MARKET_IF_TOUCHED, "EUR_USD", DEFAULT_UNITS_SIZE, DEFAULT_STOP_LOSS_PRICE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateOrderWithNullType_Exception(){
        new OrderImpl("12", null, "EUR_USD", DEFAULT_UNITS_SIZE, DEFAULT_STOP_LOSS_PRICE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateOrderWithNullInstrument_Exception(){
        new OrderImpl("12", OrderType.MARKET_IF_TOUCHED, null, DEFAULT_UNITS_SIZE, DEFAULT_STOP_LOSS_PRICE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateOrderWithNullUnits_Exception(){
        new OrderImpl("12", OrderType.MARKET_IF_TOUCHED, "EUR_USD", null, DEFAULT_STOP_LOSS_PRICE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateOrderWithNullStopLossPrice_Exception(){
        new OrderImpl("12", OrderType.MARKET_IF_TOUCHED, "EUR_USD", DEFAULT_UNITS_SIZE, null);
    }

    @Test
    public void WhenCreateOrderWithCOrrectSettings_CorrectResult(){
        OrderImpl order = new OrderImpl("12", OrderType.MARKET_IF_TOUCHED, "EUR_USD", DEFAULT_UNITS_SIZE, DEFAULT_STOP_LOSS_PRICE);

        assertEquals("12", order.getId());
        assertEquals(OrderType.MARKET_IF_TOUCHED, order.getOrderType());
        assertEquals("EUR_USD", order.getInstrument());
        assertEquals(DEFAULT_UNITS_SIZE, order.getUnits());
        assertEquals(DEFAULT_STOP_LOSS_PRICE, order.getStopLossPrice());
    }


}
