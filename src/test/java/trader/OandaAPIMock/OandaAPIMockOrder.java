package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.order.*;
import com.oanda.v20.transaction.OrderCancelTransaction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class OandaAPIMockOrder extends OandaAPIMock {

    private OrderContext mockOrderContext;
    private Order mockOrder;
    private OrderCancelResponse mockOrderCancelResponse;
    private MarketIfTouchedOrder mockMarketIfTouchedOrder;
    private OrderID mockOrderID;

    public OandaAPIMockOrder(Context context) {
        this();
        setMockContext(context);
        mockContext.order = mockOrderContext;
    }

    public OandaAPIMockOrder() {
        mockContext.order = mockOrderContext;
        mockOrderContext = mock(OrderContext.class);
        mockOrder = mock(Order.class);

        mockOrderID = mock(OrderID.class);
        mockMarketIfTouchedOrder = mock(MarketIfTouchedOrder.class);
        init();
        mockOrderCancelResponse = mock(OrderCancelResponse.class);
    }

    public Order getMockOrder() {
        return mockOrder;
    }

    public OrderCancelResponse getMockOrderCancelResponse() {
        return mockOrderCancelResponse;
    }

    public MarketIfTouchedOrder getMockMarketIfTouchedOrder() {
        return mockMarketIfTouchedOrder;
    }

    public OrderID getMockOrderID() {
        return mockOrderID;
    }

    private void init(){
        when(mockMarketIfTouchedOrder.getType())
                .thenReturn(OrderType.MARKET_IF_TOUCHED);
        when(mockMarketIfTouchedOrder.getId())
                .thenReturn(mockOrderID);
    }
}
