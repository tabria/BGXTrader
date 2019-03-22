package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.order.Order;
import com.oanda.v20.order.OrderContext;

import static org.mockito.Mockito.mock;

public class OandaAPIMockOrder extends OandaAPIMock {

    private OrderContext mockOrderContext;
    private Order mockOrder;

    public OandaAPIMockOrder(Context context) {
        this();
        setMockContext(context);
    }

    public OandaAPIMockOrder() {

        mockOrderContext = mock(OrderContext.class);
        mockContext.order = mockOrderContext;
        mockOrder = mock(Order.class);
    }

    public Order getMockOrder() {
        return mockOrder;
    }
}
