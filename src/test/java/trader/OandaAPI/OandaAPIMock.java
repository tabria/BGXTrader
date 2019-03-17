package trader.OandaAPI;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.instrument.InstrumentContext;
import com.oanda.v20.pricing.PricingContext;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.AccountUnits;
import com.oanda.v20.primitives.DateTime;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMock {

    private Context mockContext;
    private InstrumentCandlesRequest mockRequest;
    private InstrumentCandlesResponse mockResponse;
    private DateTime mockDateTime;

    public OandaAPIMock() {
        mockContext = mock(Context.class);
        mockContext.instrument = mock(InstrumentContext.class);
        mockRequest = mock(InstrumentCandlesRequest.class);
        mockResponse = mock(InstrumentCandlesResponse.class);
        mockDateTime = mock(DateTime.class);

    }

    public Context getContext() {
        return mockContext;
    }

    public InstrumentCandlesRequest getMockRequest() {
        return mockRequest;
    }

    public InstrumentCandlesResponse getMockResponse() {
        return mockResponse;
    }

    public DateTime getMockDateTime(){
        return mockDateTime;
    }

    public void setMockRequestToCandles() throws ExecuteException, RequestException {
        when(mockContext.instrument.candles(mockRequest)).thenReturn(mockResponse);
    }

    public void setMockResponseToGetCandles(List<Candlestick> candlestickList){
        when(mockResponse.getCandles()).thenReturn(candlestickList);
    }
}
