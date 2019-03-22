package trader.strategy;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.connector.ApiConnector;
import trader.order.Order;
import trader.price.Price;
import trader.strategy.BGXStrategy.BGXStrategy;
import trader.trade.entitie.Trade;
import trader.trade.service.exit_strategie.ExitStrategy;
import trader.trade.service.OrderService;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BGXStrategyTest {

    private List<Trade> trades;
    private List<Order> orders;
    private Order mockOrder;
    private ApiConnector apiConnector;
    private BGXStrategy bgxStrategy;
    private Trade mockTrade;
    private CommonTestClassMembers commonMembers;
    private OrderService mockOrderService;
    private ExitStrategy mockExitStrategy;

    @Before
    public void before() {
        trades = new ArrayList<>();
        orders = new ArrayList<>();
        mockOrderService = mock(OrderService.class);
        mockExitStrategy = mock(ExitStrategy.class);
        apiConnector = mock(ApiConnector.class);
        mockOrder = mock(Order.class);
        mockTrade = mock(Trade.class);
        commonMembers = new CommonTestClassMembers();
        applySettings();
        bgxStrategy = new BGXStrategy(apiConnector);
    }

    @Test(expected = SubmitNewOrderCalledException.class)
    public void ifNoOpenOrdersAndNoOpenTrades_SubmitNewOrder(){
        commonMembers.changeFieldObject(bgxStrategy, "orderService", mockOrderService);
        when(apiConnector.getOpenOrders()).thenReturn(new ArrayList<>());
        when(apiConnector.getOpenTrades()).thenReturn(new ArrayList<>());
        bgxStrategy.execute();
    }

    @Test(expected = CloseUnfilledOrderCalledException.class)
    public void ifNoOpenTradesButHaveOpenOrders_closeUnfilledOrder(){
        commonMembers.changeFieldObject(bgxStrategy, "orderService", mockOrderService);
        when(apiConnector.getOpenTrades()).thenReturn(new ArrayList<>());
        bgxStrategy.execute();
    }

    @Test(expected = ExitStrategyExecuteCalledException.class)
    public void ifOpenTrades_Execute(){
        commonMembers.changeFieldObject(bgxStrategy, "exitStrategy", mockExitStrategy);
        bgxStrategy.execute();
    }

    @Test
    public void testToString(){
       assertEquals("BGXStrategy", bgxStrategy.toString());
    }

    private void applySettings() {
        trades.add(mockTrade);
        orders.add(mockOrder);
        orderSettings();
        tradeSettings();
    }

    private void tradeSettings() {
        when(apiConnector.getOpenTrades()).thenReturn(trades);
        doThrow(new ExitStrategyExecuteCalledException()).when(mockExitStrategy).execute();
    }

    private void orderSettings() {
        when(apiConnector.getOpenOrders()).thenReturn(orders);
        doThrow(new SubmitNewOrderCalledException()).when(mockOrderService).submitNewOrder();
        doThrow(new CloseUnfilledOrderCalledException()).when(mockOrderService).closeUnfilledOrder();
    }

    private class CloseUnfilledOrderCalledException extends RuntimeException{};
    private class SubmitNewOrderCalledException extends RuntimeException{};
    private class ExitStrategyExecuteCalledException extends RuntimeException{};

}