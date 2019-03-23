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

public class OandaAPIMockPricing extends OandaAPIMock {

    private PricingContext mockPricingContext;
    private PricingGetRequest mockPricingGetRequest;
    private PricingGetResponse mockPricingGetResponse;

    public OandaAPIMockPricing(Context context){
        this();
        mockContext = context;
        mockContext.pricing = mockPricingContext;
    }

    public OandaAPIMockPricing() {
        mockPricingContext =  mock(PricingContext.class);
        mockPricingGetRequest = mock(PricingGetRequest.class);
        mockPricingGetResponse = mock(PricingGetResponse.class);
        init();
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

    public void setMockPricingGetResponse(PricingGetResponse response) throws ExecuteException, RequestException {
        when(mockContext.pricing.get(mockPricingGetRequest)).thenReturn(response);
    }

    private void init(){
        try {
            when(mockContext.pricing.get(mockPricingGetRequest))
                    .thenReturn(mockPricingGetResponse);
        } catch (RequestException | ExecuteException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
