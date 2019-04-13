package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.order.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMockOrder extends OandaAPIMock {

    private OrderContext mockOrderContext;
    private Order mockOrder;
    private OrderCancelResponse mockOrderCancelResponse;
    private OrderCreateRequest mockOrderCreateRequest;
    private OrderCreateResponse mockOrderCreateResponse;
    private MarketIfTouchedOrder mockMarketIfTouchedOrder;
    private OrderID mockOrderID;

    public OandaAPIMockOrder(Context context) {
        this();
        setMockContext(context);
        mockContext.order = mockOrderContext;
    }

    public OandaAPIMockOrder() {
        mockOrderContext = mock(OrderContext.class);
        mockContext.order = mockOrderContext;
        mockOrder = mock(Order.class);
        mockOrderCreateRequest = mock(OrderCreateRequest.class);
        mockOrderID = mock(OrderID.class);
        mockMarketIfTouchedOrder = mock(MarketIfTouchedOrder.class);
        mockOrderCancelResponse = mock(OrderCancelResponse.class);
        mockOrderCreateResponse = mock(OrderCreateResponse.class);
        init();

    }

    public Order getMockOrder() {
        return mockOrder;
    }

    public OrderCreateRequest getMockOrderCreateRequest() {
        return mockOrderCreateRequest;
    }

    public OrderCreateResponse getMockOrderCreateResponse() {
        return mockOrderCreateResponse;
    }

    public OrderCancelResponse getMockOrderCancelResponse() {
        return mockOrderCancelResponse;
    }

    public void setOrderCancelResponse(AccountID accountID, OrderSpecifier specifier){
        try {
            when(mockOrderContext.cancel(accountID, specifier)).thenReturn(mockOrderCancelResponse);
        } catch (RequestException | ExecuteException e) {
            e.printStackTrace();
        }
    }

    public MarketIfTouchedOrder getMockMarketIfTouchedOrder() {
        return mockMarketIfTouchedOrder;
    }

    public OrderID getMockOrderID() {
        return mockOrderID;
    }

    public <T extends Throwable> void setMockOrderCreateResponseToThrowException(Class<T> exception) throws ExecuteException, RequestException {
        when(mockContext.order.create(mockOrderCreateRequest)).thenThrow(exception);
    }

    public <T extends Throwable> void setMockOrderCancelResponseToThrowException(Class<T> exception) throws ExecuteException, RequestException {
        when(mockContext.order.cancel(any(AccountID.class), any(OrderSpecifier.class))).thenThrow(exception);
    }

    private void init(){
        when(mockMarketIfTouchedOrder.getType())
                .thenReturn(OrderType.MARKET_IF_TOUCHED);
        when(mockMarketIfTouchedOrder.getId())
                .thenReturn(mockOrderID);
        try {
            when(mockOrderContext.create(mockOrderCreateRequest)).thenReturn(mockOrderCreateResponse);
        } catch (RequestException | ExecuteException e) {
            e.printStackTrace();
        }
    }
}
