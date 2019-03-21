package trader.trade;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.account.AccountContext;
import com.oanda.v20.account.AccountGetResponse;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.order.Order;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.TradeSummary;
import org.junit.Before;
import org.junit.Test;
import trader.trade.service.exit_strategie.HalfCloseTrailExitStrategy;
import trader.trade.service.NewTradeService;
import trader.trade.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class PositionManagerTest {

    private PositionManager positionManager;
    private Context mockContext;
    private NewTradeService mockNewTradeService;
    private HalfCloseTrailExitStrategy mockHalfCloseTrailExitStrategy;
    private OrderService mockOrderService;
    private DateTime mockDateTime;
    private AccountGetResponse mockAccountGetResponse;
    private Account mockAccount;
    private TradeSummary mockTradeSummary;
    private Order mockOrder;
    private List<TradeSummary> trades;
    private List<Order> orders;
    private BigDecimal ask = BigDecimal.ONE;
    private BigDecimal bid = BigDecimal.TEN;

    @Before
    public void before() throws Exception {

        this.mockTradeSummary = mock(TradeSummary.class);
        this.mockOrder = mock(Order.class);

        this.trades = new ArrayList<>();
        this.trades.add(this.mockTradeSummary);

        this.orders = new ArrayList<>();
        this.orders.add(this.mockOrder);

        this.mockAccount = mock(Account.class);
        when(this.mockAccount.getTrades()).thenReturn(this.trades);
        when(this.mockAccount.getOrders()).thenReturn(this.orders);

        this.mockAccountGetResponse = mock(AccountGetResponse.class);
        when(this.mockAccountGetResponse.getAccount()).thenReturn(this.mockAccount);

        this.mockContext = mock(Context.class);
        this.mockContext.account = mock(AccountContext.class);
        when(this.mockContext.account.get(any(AccountID.class))).thenReturn(this.mockAccountGetResponse);

        this.mockNewTradeService = mock(NewTradeService.class);
        doThrow(RuntimeException.class).when(this.mockNewTradeService).sendNewTradeOrder(any(Account.class), any(BigDecimal.class));

        this.mockHalfCloseTrailExitStrategy = mock(HalfCloseTrailExitStrategy.class);
        doThrow(IllegalArgumentException.class).when(this.mockHalfCloseTrailExitStrategy).execute(any(Account.class), any(BigDecimal.class), any(BigDecimal.class), any(DateTime.class));

        this.mockOrderService = mock(OrderService.class);
        doThrow(NullPointerException.class).when(this.mockOrderService).closeUnfilledOrder(any(Account.class), any(BigDecimal.class), any(BigDecimal.class));

        this.mockDateTime = mock(DateTime.class);


        this.positionManager = new PositionManager(this.mockContext, this.mockNewTradeService, this.mockHalfCloseTrailExitStrategy, this.mockOrderService);
    }


    @Test(expected = NullPointerException.class)
    public void WhenContextIsNullThenException(){
        new PositionManager(null, this.mockNewTradeService, this.mockHalfCloseTrailExitStrategy, this.mockOrderService);
    }

    @Test(expected = NullPointerException.class)
    public void WhenNewTradeManagerIsNullThenException(){
        new PositionManager(this.mockContext, null, this.mockHalfCloseTrailExitStrategy, this.mockOrderService);
    }

    @Test(expected = NullPointerException.class)
    public void WhenExistingTradeManagerIsNullThenException(){
        new PositionManager(this.mockContext, this.mockNewTradeService, null, this.mockOrderService);
    }

    @Test(expected = NullPointerException.class)
    public void WhenOrderManagerIsNullThenException(){
        new PositionManager(this.mockContext, this.mockNewTradeService, this.mockHalfCloseTrailExitStrategy, null);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallUpdateThenNewTradeManagerCalledCorrectly(){

       this.trades.remove(0);
       this.orders.remove(0);

        this.positionManager.updateObserver(this.mockDateTime, this.ask, this.bid);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCallUpdateThenExistingManagerCalledCorrectly(){

        doNothing().when(this.mockNewTradeService).sendNewTradeOrder(any(Account.class), any(BigDecimal.class));
        doNothing().when(this.mockOrderService).closeUnfilledOrder(any(Account.class), any(BigDecimal.class), any(BigDecimal.class));

        this.positionManager.updateObserver(this.mockDateTime, this.ask, this.bid);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallUpdateThenOrderManagerCalledCorrectly(){

        this.trades.remove(0);

        this.positionManager.updateObserver(this.mockDateTime, this.ask, this.bid);

    }
}