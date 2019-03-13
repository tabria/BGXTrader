package trader.OandaAPI;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.instrument.InstrumentContext;

import static org.mockito.Mockito.mock;

public class OandaAPIMock {
    private Context mockContext;
    private InstrumentCandlesRequest mockRequest;
    private InstrumentCandlesResponse mockResponse;

    public OandaAPIMock(){
        mockContext = mock(Context.class);
        mockContext.instrument = mock(InstrumentContext.class);
        mockRequest = mock(InstrumentCandlesRequest.class);
        mockResponse = mock(InstrumentCandlesResponse.class);
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
}
