package trader.broker.connector.oanda;

import com.oanda.v20.pricing.PricingGetRequest;
import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;

public class OandaPriceRequestTest {


    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private OandaPriceRequest request;
    private HashMap<String, String> settings;
    private static final String EXPECTED_ACCOUNT_ID = "100";
    private static final String EXPECTED_INSTRUMENT = "EUR_USD";

    @Before
    public void setUp() {
        request = new OandaPriceRequest();
        settings = new HashMap<>();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithNullSettings_Exception(){
        request.createRequest(null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithoutAccountIdKeyNameInSettings_Exception(){
        setRequestSettings("accou", EXPECTED_ACCOUNT_ID, INSTRUMENT, EXPECTED_INSTRUMENT);
        request.createRequest(settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithAccountIdKeyNameNull_Exception(){
        setRequestSettings(null, EXPECTED_ACCOUNT_ID, INSTRUMENT, EXPECTED_INSTRUMENT);
        request.createRequest(settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithAccountIdValueNullInSettings_Exception(){
        setRequestSettings(ACCOUNT_ID, null, INSTRUMENT, EXPECTED_INSTRUMENT);
        request.createRequest(settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithoutInstrumentKeyNameInSettings_Exception(){
        setRequestSettings(ACCOUNT_ID, EXPECTED_ACCOUNT_ID,"ins",EXPECTED_INSTRUMENT);
        request.createRequest(settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithInstrumentKeyNameNull_Exception(){
        setRequestSettings(ACCOUNT_ID, EXPECTED_ACCOUNT_ID,null,EXPECTED_INSTRUMENT);
        request.createRequest(settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCreateRequestWithInstrumentValueNullInSettings_Exception(){
        setRequestSettings(ACCOUNT_ID, EXPECTED_ACCOUNT_ID,INSTRUMENT,null);
        request.createRequest(settings);
    }

    @Test
    public void getCorrectPriceRequest(){
        setRequestSettings(ACCOUNT_ID, EXPECTED_ACCOUNT_ID,INSTRUMENT,EXPECTED_INSTRUMENT);
        PricingGetRequest request = this.request.createRequest(settings);

        assertEquals(PricingGetRequest.class, getRequestClass());
        assertEquals(EXPECTED_ACCOUNT_ID, getRequestAccount(request));
        assertEquals(EXPECTED_INSTRUMENT, getRequestInstrument(request));
    }

    private Class<? extends PricingGetRequest> getRequestClass() {
        return this.request.createRequest(settings).getClass();
    }

    private String getRequestAccount(PricingGetRequest request) {
        return request.getPathParams().get(ACCOUNT_ID).toString();
    }

    private String getRequestInstrument(PricingGetRequest request) {
        ArrayList instrumentsList = (ArrayList) request.getQueryParams().get(INSTRUMENT +"s");
        return instrumentsList.get(0).toString();
    }

    private void setRequestSettings(String accountName, String accountValue, String instrumentName, String instrumentValue){
        settings.put(accountName, accountValue);
        settings.put(instrumentName, instrumentValue );
    }
}
