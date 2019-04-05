package trader.broker.connector.oanda;

import com.oanda.v20.pricing.PricingGetRequest;
import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

public class OandaPriceRequestTest {

    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private OandaPriceRequest request;
    private static final String EXPECTED_ACCOUNT_ID = "100";
    private static final String EXPECTED_INSTRUMENT = "EUR_USD";

    @Before
    public void setUp() {
        request = new OandaPriceRequest();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithNullSettings_Exception(){
        request.createRequest(null, null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithAccountIdKeyNameNull_Exception(){
        request.createRequest(null, EXPECTED_INSTRUMENT);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithInstrumentKeyNameNull_Exception(){
        request.createRequest(EXPECTED_ACCOUNT_ID,null);
    }

    @Test
    public void getCorrectPriceRequest(){
        PricingGetRequest pricingRequest = this.request.createRequest(EXPECTED_ACCOUNT_ID, EXPECTED_INSTRUMENT);

        assertEquals(PricingGetRequest.class, getRequestClass(pricingRequest));
        assertEquals(EXPECTED_ACCOUNT_ID, getRequestAccount(pricingRequest));
        assertEquals(EXPECTED_INSTRUMENT, getRequestInstrument(pricingRequest));
    }

    private Class<? extends PricingGetRequest> getRequestClass(PricingGetRequest request) {
        return request.getClass();
    }

    private String getRequestAccount(PricingGetRequest request) {
        return request.getPathParams().get(ACCOUNT_ID).toString();
    }

    private String getRequestInstrument(PricingGetRequest request) {
        ArrayList instrumentsList = (ArrayList) request.getQueryParams().get(INSTRUMENT +"s");
        return instrumentsList.get(0).toString();
    }
}
