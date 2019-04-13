package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.trade.TradeContext;
import com.oanda.v20.trade.TradeSetDependentOrdersRequest;
import com.oanda.v20.trade.TradeSetDependentOrdersResponse;
import com.oanda.v20.trade.TradeSummary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMockTrade extends OandaAPIMock {

    private TradeContext mockTradeContext;
    private TradeSummary mockTradeSummary;
    private TradeSetDependentOrdersRequest tradeSetDependentOrdersRequestMock;
    private TradeSetDependentOrdersResponse tradeSetDependentOrdersResponseMock;


    public OandaAPIMockTrade(Context context) {
        this();
        setMockContext(context);
    }

    public OandaAPIMockTrade() {
        tradeSetDependentOrdersRequestMock = mock(TradeSetDependentOrdersRequest.class);
        tradeSetDependentOrdersResponseMock = mock(TradeSetDependentOrdersResponse.class);
        mockTradeContext = mock(TradeContext.class);
        mockContext.trade = mockTradeContext;
        mockTradeSummary = mock(TradeSummary.class);
    }

    public TradeSummary getMockTradeSummary() {
        return mockTradeSummary;
    }

    public TradeSetDependentOrdersRequest getTradeSetDependentOrdersRequestMock() {
        return tradeSetDependentOrdersRequestMock;
    }

    public TradeSetDependentOrdersResponse getTradeSetDependentOrdersResponseMock() {
        return tradeSetDependentOrdersResponseMock;
    }

    public void setSetDependentOrdersMock(){
        try {
            when(mockTradeContext.setDependentOrders(tradeSetDependentOrdersRequestMock)).thenReturn(tradeSetDependentOrdersResponseMock);
        } catch (RequestException | ExecuteException e) {
            e.printStackTrace();
        }
    }

    public <T extends Throwable> void setDependentOrderResponseToThrowException(Class<T> exception) throws ExecuteException, RequestException {
        when(mockContext.trade.setDependentOrders(any(TradeSetDependentOrdersRequest.class))).thenThrow(exception);
    }
}
