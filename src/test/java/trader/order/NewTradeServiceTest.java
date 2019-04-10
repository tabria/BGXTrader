package trader.order;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.order.*;
import com.oanda.v20.order.Order;
import com.oanda.v20.primitives.AccountUnits;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.TradeSummary;
import com.oanda.v20.transaction.Transaction;
import com.oanda.v20.transaction.TransactionID;
import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.TradeImpl;
import trader.entry.StandardEntryStrategy;
import trader.order.NewTradeService;
import trader.entity.trade.Direction;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewTradeServiceTest {


    private static final String TRANSACTION_ID = "12";
    private static final String DATE_TIME = "11:11:11T01:01:01Z";

    private Context mockContext;
    private StandardEntryStrategy mockStandardEntryStrategy;
    private com.oanda.v20.order.Order mockOrder;
    private TradeSummary mockTradeSummary;
    private List<com.oanda.v20.order.Order> orderList;
    private List<TradeSummary> tradeSummaries;
    private Account mockAccount;
    private TradeImpl mockTradeImpl;
    private BigDecimal bid;
    private NewTradeService newTradeService;
    private AccountUnits mockAccountUnits;
    private AccountUnits mockMarginAvailable;
    private AccountUnits mockMarginUsed;
    private OrderCreateResponse mockOrderCreateResponse;
    private Transaction mockTransaction;
    private TransactionID mockTransactionID;
    private DateTime mockDateTime;

    @Before
    public void before() throws Exception {

        this.mockContext = mock(Context.class);
        this.mockContext.order = mock(OrderContext.class);

        this.mockOrderCreateResponse = mock(OrderCreateResponse.class);
        this.mockTransaction = mock(Transaction.class);
        this.mockTransactionID = mock(TransactionID.class);
        when(this.mockTransactionID.toString()).thenReturn(TRANSACTION_ID);
        when(this.mockTransaction.getId()).thenReturn(this.mockTransactionID);

        this.mockDateTime = mock(DateTime.class);
        when(this.mockDateTime.toString()).thenReturn(DATE_TIME);
        when(this.mockTransaction.getTime()).thenReturn(this.mockDateTime);
        when(this.mockOrderCreateResponse.getOrderCreateTransaction()).thenReturn(this.mockTransaction);

        when(this.mockContext.order.create(any(OrderCreateRequest.class))).thenReturn(this.mockOrderCreateResponse);


        this.mockTradeImpl = mock(TradeImpl.class);
        when(this.mockTradeImpl.getEntryPrice()).thenReturn(BigDecimal.valueOf(1.14223));
        when(this.mockTradeImpl.getStopLossPrice()).thenReturn(BigDecimal.valueOf(1.14551));
        when(this.mockTradeImpl.getDirection()).thenReturn(Direction.DOWN);
        when(this.mockTradeImpl.getTradable()).thenReturn(true);

        this.mockStandardEntryStrategy = mock(StandardEntryStrategy.class);
        when(this.mockStandardEntryStrategy.generateTrade()).thenReturn(this.mockTradeImpl);
      //  when(this.mockStandardEntryStrategy.isGenerated()).thenReturn(false);

        this.mockOrder = mock(com.oanda.v20.order.Order.class);
        this.orderList = fillOrderList(OrderType.STOP_LOSS, 3);

        this.mockTradeSummary = mock(TradeSummary.class);
        this.tradeSummaries = fillTradeSummaries(0);

        this.mockAccountUnits = mock(AccountUnits.class);
        when(this.mockAccountUnits.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1000));

        this.mockMarginAvailable = mock(AccountUnits.class);
        when(this.mockMarginAvailable.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1000));

        this.mockMarginUsed = mock(AccountUnits.class);
        when(this.mockMarginUsed.bigDecimalValue()).thenReturn(BigDecimal.valueOf(1));

        this.mockAccount = mock(Account.class);
        when(this.mockAccount.getOrders()).thenReturn(this.orderList);
        when(this.mockAccount.getTrades()).thenReturn(this.tradeSummaries);
        when(this.mockAccount.getBalance()).thenReturn(this.mockAccountUnits);
        when(this.mockAccount.getMarginAvailable()).thenReturn(this.mockMarginAvailable);
        when(this.mockAccount.getMarginUsed()).thenReturn(this.mockMarginUsed);

        this.bid = BigDecimal.valueOf(1.14064);

        this.newTradeService = new NewTradeService(this.mockContext, this.mockStandardEntryStrategy);

    }


    @Test(expected = NullPointerException.class)
    public void WhenCreateNewTradeManagerWithNullBGXTradeGeneratorThenException(){
        new NewTradeService(this.mockContext, null);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewTradeManagerWithContextThenException(){
        new NewTradeService(null, this.mockStandardEntryStrategy);
    }


    @Test
    public void WhenHaveWaitingTradesToBeFillThenManageNewTradeDoNotSendNewOrderRequest() throws NoSuchFieldException, IllegalAccessException {

        //getting hasWaitingTrades value
        OrderCreateResponse previous = this.getOrderResponse();

        this.orderList = fillOrderList(OrderType.MARKET_IF_TOUCHED, 3);

        when(this.mockAccount.getOrders()).thenReturn(this.orderList);

        this.newTradeService.sendNewTradeOrder(mockAccount, bid);
        OrderCreateResponse current = this.getOrderResponse();

        assertEquals(previous, current);

    }

    @Test
    public void WhenHaveOpenTradesThenManageNewTradeDoNotSendNewOrderRequest() throws NoSuchFieldException, IllegalAccessException {

        //getting hasWaitingTrades value
        OrderCreateResponse previous = this.getOrderResponse();

        this.tradeSummaries = fillTradeSummaries(1);

        when(this.mockAccount.getTrades()).thenReturn(this.tradeSummaries);

        this.newTradeService.sendNewTradeOrder(mockAccount, bid);
        OrderCreateResponse current = this.getOrderResponse();

        assertEquals(previous, current);

    }

    //if isGenerated() is true => trade has been generated and order has been sent
    @Test
    public void WhenGeneratedTradeIsTrueThenManageNewTradeDoNotSendNewOrderRequest() throws NoSuchFieldException, IllegalAccessException {

        OrderCreateResponse previous = this.getOrderResponse();

  //      when(this.mockStandardEntryStrategy.isGenerated()).thenReturn(true);

        this.newTradeService.sendNewTradeOrder(mockAccount, bid);
        OrderCreateResponse current = this.getOrderResponse();

        assertEquals(previous, current);

    }

    @Test
    public void WhenUnitSizeIsZeroThenManageNewTradeDoNotSendOrderRequest() throws NoSuchFieldException, IllegalAccessException {


        when(this.mockMarginAvailable.bigDecimalValue()).thenReturn(BigDecimal.valueOf(123456789));
        when(this.mockMarginUsed.bigDecimalValue()).thenReturn(BigDecimal.valueOf(123));
        when(this.mockAccountUnits.bigDecimalValue()).thenReturn(BigDecimal.valueOf(0));

        this.newTradeService.sendNewTradeOrder(mockAccount, bid);

        OrderCreateResponse current = this.getOrderResponse();

        assertNull(current);


    }


    @Test
    public void WhenCorrectValuesThenManageNewTradeReturnResponseObject() throws NoSuchFieldException, IllegalAccessException {

        this.newTradeService.sendNewTradeOrder(mockAccount, bid);

        OrderCreateResponse current = this.getOrderResponse();

        assertSame(this.mockOrderCreateResponse, current);
    }



    private OrderCreateResponse getOrderResponse() throws NoSuchFieldException, IllegalAccessException {
        Field hasResponse = this.newTradeService.getClass().getDeclaredField("orderCreateResponse");
        hasResponse.setAccessible(true);
        return (OrderCreateResponse) hasResponse.get(this.newTradeService);
    }

    private List<com.oanda.v20.order.Order> fillOrderList(OrderType type, int size){
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i <size ; i++) {
            when(this.mockOrder.getType()).thenReturn(type);
            orders.add(this.mockOrder);
        }
        return orders;
    }

    private List<TradeSummary> fillTradeSummaries(int size) {
        List<TradeSummary> trades = new ArrayList<>();
        for (int i = 0; i <size ; i++) {
            trades.add(this.mockTradeSummary);
        }
        return trades;
    }

}