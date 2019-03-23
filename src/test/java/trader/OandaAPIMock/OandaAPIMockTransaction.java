package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.order.MarketIfTouchedOrder;
import com.oanda.v20.transaction.OrderCancelTransaction;
import com.oanda.v20.transaction.StopLossDetails;
import com.oanda.v20.transaction.TransactionContext;
import com.oanda.v20.transaction.TransactionID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMockTransaction extends OandaAPIMock {

    private TransactionContext mockTransactionContext;
    private TransactionID mockTransactionID;
    private OrderCancelTransaction mockOrderCancelTransaction;
    private StopLossDetails mockStopLossDetails;


    public OandaAPIMockTransaction(Context context){
        this();
        mockContext = context;
        mockContext.transaction = mockTransactionContext;
    }

    public OandaAPIMockTransaction() {
        mockOrderCancelTransaction = mock(OrderCancelTransaction.class);
        mockTransactionContext = mock(TransactionContext.class);
        mockTransactionID = mock(TransactionID.class);

        mockStopLossDetails = mock(StopLossDetails.class);
        init();

    }

    public TransactionID getMockTransactionID() {
        return mockTransactionID;
    }

    public OrderCancelTransaction getMockOrderCancelTransaction() {
        return mockOrderCancelTransaction;
    }

    public StopLossDetails getMockStopLossDetails() {
        return mockStopLossDetails;
    }

    private void init(){
        when(mockOrderCancelTransaction.getId()).thenReturn(mockTransactionID);
        when(mockOrderCancelTransaction.getTime()).thenReturn(mockDateTime);
    }
}
