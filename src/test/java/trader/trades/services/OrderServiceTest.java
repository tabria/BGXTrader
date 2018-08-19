package trader.trades.services;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.order.*;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.primitives.DecimalNumber;
import com.oanda.v20.transaction.OrderCancelTransaction;
import com.oanda.v20.transaction.StopLossDetails;
import com.oanda.v20.transaction.TransactionID;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private static final String TRANSACTION_ID = "12";
    private static final String DATE_TIME = "11:11:11T01:01:01Z";

    private Context mockContext;
    private Account mockAccount;
    private MarketIfTouchedOrder mockOrder;
    private StopLossDetails mockStopLossDetails;
    private PriceValue mockStopLossPrice;
    private DecimalNumber mockDecimalNumber;
    private OrderCancelResponse mockOrderCancelResponse;
    private List<Order> orderList;
    private OrderService orderService;
    private OrderCancelTransaction mockOrderCancelTransaction;
    private TransactionID mockTransactionID;
    private DateTime mockDateTime;

    @Before
    public void before() throws Exception {


        this.mockOrderCancelResponse = mock(OrderCancelResponse.class);
        this.mockOrderCancelTransaction = mock(OrderCancelTransaction.class);
        this.mockTransactionID = mock(TransactionID.class);
        when(this.mockTransactionID.toString()).thenReturn(TRANSACTION_ID);
        when(this.mockOrderCancelTransaction.getId()).thenReturn(this.mockTransactionID);

        this.mockDateTime = mock(DateTime.class);
        when(this.mockDateTime.toString()).thenReturn(DATE_TIME);
        when(this.mockOrderCancelTransaction.getTime()).thenReturn(this.mockDateTime);
        when(this.mockOrderCancelResponse.getOrderCancelTransaction()).thenReturn(this.mockOrderCancelTransaction);

        //context
        this.mockContext = mock(Context.class);
        this.mockContext.order = mock(OrderContext.class);
        when(this.mockContext.order.cancel(any(AccountID.class), any(OrderSpecifier.class))).thenReturn(this.mockOrderCancelResponse);

        this.orderList = new ArrayList<>();

        this.mockStopLossPrice = mock(PriceValue.class);

        this.mockStopLossDetails = mock(StopLossDetails.class);
        when(this.mockStopLossDetails.getPrice()).thenReturn(this.mockStopLossPrice);

        this.mockDecimalNumber = mock(DecimalNumber.class);

        //order
        this.mockOrder = mock(MarketIfTouchedOrder.class);
        when(this.mockOrder.getType()).thenReturn(OrderType.MARKET_IF_TOUCHED);
        when(this.mockOrder.getStopLossOnFill()).thenReturn(this.mockStopLossDetails);
        when(this.mockOrder.getUnits()).thenReturn(this.mockDecimalNumber);
        when(this.mockOrder.getId()).thenReturn(mock(OrderID.class));

        //account
        this.mockAccount = mock(Account.class);
        when(this.mockAccount.getOrders()).thenReturn(this.orderList);
        when(this.mockAccount.getId()).thenReturn(mock(AccountID.class));

        this.orderService = new OrderService(this.mockContext);

    }

    @Test(expected = NullPointerException.class)
    public void WhenContextIsNullThenException(){
        new OrderService(null);
    }



    @Test
    public void WhenNoWaitingOrderThanNoAction() throws NoSuchFieldException, IllegalAccessException {

        assertEquals("Order List must be empty", 0, this.orderList.size());
        assertNull(getOrderCancelResponse());

    }

    @Test
    public void WhenWaitingOrderIsNotMarket_If_TouchedThenNoAction() throws NoSuchFieldException, IllegalAccessException {

        StopLossOrder slo = mock(StopLossOrder.class);
        when(slo.getType()).thenReturn(OrderType.STOP_LOSS);

        this.orderList.add(slo);

        assertEquals("Order List must have 1 entry", 1, this.orderList.size());
        assertNull(getOrderCancelResponse());
    }

    @Test
    public void WhenUnitsNegativeAndDeltaBiggerThanOffsetThenCancelOrder() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(-50));
        when(this.mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));

        this.orderList.add(this.mockOrder);

        BigDecimal ask = BigDecimal.valueOf(1.14252);
        BigDecimal bid = BigDecimal.valueOf(1.14150);

        this.orderService.closeUnfilledOrder(this.mockAccount, ask, bid);

        assertSame(this.mockOrderCancelResponse, this.getOrderCancelResponse());

    }

    @Test
    public void WhenUnitsNegativeAndPriceBelowStopLossThenNoAction() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(-50));
        when(this.mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));

        this.orderList.add(this.mockOrder);

        BigDecimal ask = BigDecimal.valueOf(1.14119);
        BigDecimal bid = BigDecimal.valueOf(1.14200);

        this.orderService.closeUnfilledOrder(this.mockAccount, ask, bid);

        assertNull(getOrderCancelResponse());

    }

    @Test
    public void WhenUnitsPositiveAndPriceLowerThanStopLossThenCancelOrder() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(50));
        when(this.mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));

        this.orderList.add(this.mockOrder);

        BigDecimal ask = BigDecimal.valueOf(1.14252);
        BigDecimal bid = BigDecimal.valueOf(1.14150);

        this.orderService.closeUnfilledOrder(this.mockAccount, ask, bid);

        assertSame(this.mockOrderCancelResponse, this.getOrderCancelResponse());

    }

    @Test
    public void WhenUnitsPositiveAndPriceHigherThanStopLossThenNoAction() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(50));
        when(this.mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));

        this.orderList.add(this.mockOrder);

        BigDecimal ask = BigDecimal.valueOf(1.14203);
        BigDecimal bid = BigDecimal.valueOf(1.14202);

        this.orderService.closeUnfilledOrder(this.mockAccount, ask, bid);

        assertNull(getOrderCancelResponse());

    }

    private OrderCancelResponse getOrderCancelResponse() throws NoSuchFieldException, IllegalAccessException {
        Field cancelOrderResponse = this.orderService.getClass().getDeclaredField("cancelOrderResponse");
        cancelOrderResponse.setAccessible(true);
        return (OrderCancelResponse) cancelOrderResponse.get(this.orderService);
    }
}