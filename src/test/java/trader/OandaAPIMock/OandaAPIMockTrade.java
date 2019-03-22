package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.trade.TradeContext;
import com.oanda.v20.trade.TradeSummary;

import static org.mockito.Mockito.mock;

public class OandaAPIMockTrade extends OandaAPIMock {

    private TradeContext mockTradeContext;
    private TradeSummary mockTradeSummary;


    public OandaAPIMockTrade(Context context) {
        this();
        setMockContext(context);
    }

    public OandaAPIMockTrade() {

        mockTradeContext = mock(TradeContext.class);
        mockContext.trade = mockTradeContext;
        mockTradeSummary = mock(TradeSummary.class);

    }

    public TradeSummary getMockTradeSummary() {
        return mockTradeSummary;
    }
}
