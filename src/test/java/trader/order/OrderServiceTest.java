package trader.order;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.order.*;
import com.oanda.v20.order.Order;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DecimalNumber;
import com.oanda.v20.transaction.OrderCancelTransaction;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.OandaAPIMock.OandaAPIMockOrder;
import trader.OandaAPIMock.OandaAPIMockTransaction;
import trader.order.OrderService;

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
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaAPIMockOrder oandaAPIMockOrder;
    private OandaAPIMockTransaction oandaAPIMockTransaction;
    private PriceValue mockStopLossPrice;
    private DecimalNumber mockDecimalNumber;
    private OrderCancelResponse mockOrderCancelResponse;
    private List<Order> orderList;
    private OrderService orderService;
    private OrderCancelTransaction mockOrderCancelTransaction;

    @Before
    public void before() throws Exception {

        mockContext = mock(Context.class);
        oandaAPIMockAccount = new OandaAPIMockAccount(mockContext);
        oandaAPIMockOrder = new OandaAPIMockOrder(mockContext);
        oandaAPIMockTransaction = new OandaAPIMockTransaction(mockContext);
        this.orderList = new ArrayList<>();
        this.mockStopLossPrice = mock(PriceValue.class);
        this.mockDecimalNumber = mock(DecimalNumber.class);
        settings();
        this.orderService = new OrderService(this.mockContext);

    }
    @Test
    public void WhenNoWaitingOrderThanNoAction() throws NoSuchFieldException, IllegalAccessException {

        assertEquals("Order List must be empty", 0, orderList.size());
        assertNull(getOrderCancelResponse());

    }

    @Test
    public void WhenWaitingOrderIsNotMarket_If_TouchedThenNoAction() throws NoSuchFieldException, IllegalAccessException {

        StopLossOrder slo = mock(StopLossOrder.class);
        when(slo.getType()).thenReturn(OrderType.STOP_LOSS);

        orderList.add(slo);

        assertEquals("Order List must have 1 entry", 1, orderList.size());
        assertNull(getOrderCancelResponse());
    }

    @Test
    public void WhenUnitsNegativeAndDeltaBiggerThanOffsetThenCancelOrder() throws NoSuchFieldException, IllegalAccessException {

        when(mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(-50));
        when(mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));
        orderList.add(oandaAPIMockOrder.getMockMarketIfTouchedOrder());

        BigDecimal ask = BigDecimal.valueOf(1.14252);
        BigDecimal bid = BigDecimal.valueOf(1.14150);

        orderService.closeUnfilledOrder(oandaAPIMockAccount.getMockAccount(), ask, bid);

        assertSame(mockOrderCancelResponse, this.getOrderCancelResponse());

    }

    @Test
    public void WhenUnitsNegativeAndPriceBelowStopLossThenNoAction() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(-50));
        when(this.mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));

        this.orderList.add(oandaAPIMockOrder.getMockMarketIfTouchedOrder());

        BigDecimal ask = BigDecimal.valueOf(1.14119);
        BigDecimal bid = BigDecimal.valueOf(1.14200);

        this.orderService.closeUnfilledOrder(oandaAPIMockAccount.getMockAccount(), ask, bid);

        assertNull(getOrderCancelResponse());

    }

    @Test
    public void WhenUnitsPositiveAndPriceLowerThanStopLossThenCancelOrder() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(50));
        when(this.mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));

        this.orderList.add(oandaAPIMockOrder.getMockMarketIfTouchedOrder());

        BigDecimal ask = BigDecimal.valueOf(1.14252);
        BigDecimal bid = BigDecimal.valueOf(1.14150);

        this.orderService.closeUnfilledOrder(oandaAPIMockAccount.getMockAccount(), ask, bid);

        assertSame(mockOrderCancelResponse, this.getOrderCancelResponse());

    }

    @Test
    public void WhenUnitsPositiveAndPriceHigherThanStopLossThenNoAction() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockDecimalNumber.bigDecimalValue()).thenReturn(BigDecimal.valueOf(50));
        when(this.mockStopLossPrice.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1.14201));

        this.orderList.add(oandaAPIMockOrder.getMockMarketIfTouchedOrder());

        BigDecimal ask = BigDecimal.valueOf(1.14203);
        BigDecimal bid = BigDecimal.valueOf(1.14202);

        this.orderService.closeUnfilledOrder(oandaAPIMockAccount.getMockAccount(), ask, bid);

        assertNull(getOrderCancelResponse());

    }


    private void settings() throws RequestException, ExecuteException {
        this.mockOrderCancelResponse = mock(OrderCancelResponse.class);
        this.mockOrderCancelTransaction = mock(OrderCancelTransaction.class);
        when(oandaAPIMockTransaction.getMockTransactionID().toString()).thenReturn(TRANSACTION_ID);
        when(this.mockOrderCancelTransaction.getId())
                .thenReturn(oandaAPIMockTransaction.getMockTransactionID());
        when(oandaAPIMockOrder.getMockDateTime().toString()).thenReturn(DATE_TIME);
        when(oandaAPIMockTransaction.getMockOrderCancelTransaction().getTime()).thenReturn(this.oandaAPIMockTransaction.getMockDateTime());
        when(this.mockOrderCancelResponse.getOrderCancelTransaction()).thenReturn(this.mockOrderCancelTransaction);
        when(this.mockContext.order.cancel(any(AccountID.class), any(OrderSpecifier.class))).thenReturn(this.mockOrderCancelResponse);
        when(oandaAPIMockTransaction.getMockStopLossDetails().getPrice()).thenReturn(this.mockStopLossPrice);
        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getType()).thenReturn(OrderType.MARKET_IF_TOUCHED);
        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getStopLossOnFill()).thenReturn(oandaAPIMockTransaction.getMockStopLossDetails());
        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getUnits()).thenReturn(this.mockDecimalNumber);
        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getId()).thenReturn(mock(OrderID.class));


        when(oandaAPIMockAccount.getMockAccount().getOrders())
                .thenReturn(this.orderList);
        when(oandaAPIMockAccount.getMockAccount().getId())
                .thenReturn(mock(AccountID.class));
    }

    private OrderCancelResponse getOrderCancelResponse() throws NoSuchFieldException, IllegalAccessException {
        Field cancelOrderResponse = this.orderService.getClass().getDeclaredField("cancelOrderResponse");
        cancelOrderResponse.setAccessible(true);
        return (OrderCancelResponse) cancelOrderResponse.get(this.orderService);
    }

//    private void setMockOrder() {
////        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getType())
////                .thenReturn(OrderType.MARKET_IF_TOUCHED);
//        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getStopLossOnFill()).thenReturn(oandaAPIMockTransaction.getMockStopLossDetails());
//        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getUnits())
//                .thenReturn(this.mockDecimalNumber);
////        when(oandaAPIMockOrder.getMockMarketIfTouchedOrder().getId())
////                .thenReturn(oandaAPIMockOrder.getMockOrderID());
//    }
}