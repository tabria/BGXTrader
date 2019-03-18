package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.PricingContext;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.DateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMockPricing {

    private Context mockContext;
    private DateTime mockDateTime;
    private PricingGetRequest mockPricingGetRequest;
    private PricingGetResponse mockPricingGetResponse;

    public OandaAPIMockPricing() {
        mockContext = mock(Context.class);
        mockContext.pricing = mock(PricingContext.class);
        mockDateTime = mock(DateTime.class);
        mockPricingGetRequest = mock(PricingGetRequest.class);
        mockPricingGetResponse = mock(PricingGetResponse.class);
    }

    public Context getMockContext() {
        return mockContext;
    }

    public PricingGetRequest getMockPricingGetRequest() {
        return mockPricingGetRequest;
    }

    public PricingGetResponse getMockPricingGetResponse() {
        return mockPricingGetResponse;
    }

    public void setMockPricingGetRequest() throws ExecuteException, RequestException {
        when(mockContext.pricing.get(mockPricingGetRequest)).thenReturn(mockPricingGetResponse);
    }

    public void setMockPricingGetResponse(PricingGetResponse response) throws ExecuteException, RequestException {
        when(mockContext.pricing.get(mockPricingGetRequest)).thenReturn(response);
    }
}
