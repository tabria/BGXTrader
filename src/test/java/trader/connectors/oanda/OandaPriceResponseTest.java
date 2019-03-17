package trader.connectors.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import org.junit.Before;
import trader.CommonTestClassMembers;
import trader.OandaAPI.OandaAPIMockPricing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class OandaPriceResponseTest {

    OandaAPIMockPricing oandaAPIMockPricing;
    OandaPriceResponse priceResponse;
    CommonTestClassMembers commonMembers;
    OandaConnector mockOandaConnector;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMockPricing = new OandaAPIMockPricing();
        mockOandaConnector = mock(OandaConnector.class);
        oandaAPIMockPricing.setMockPricingGetResponse();
        priceResponse = new OandaPriceResponse(mockOandaConnector);
        commonMembers = new CommonTestClassMembers();
    }

//    @Test
//    public void getCorrectPriceRequest(){
//
//        PricingGetRequest priceRequest = (PricingGetRequest) commonMembers.extractFieldObject(priceResponse, "pricingGetRequest");
//
//        assertTrue(priceRequest.getPathParams().containsValue(oandaAPIMock.getMockAccountID()));
//    }
//
//    @Test
//    public void getPrice(){
//        ZonedDateTime currentTime = ZonedDateTime.now();
//        Price price = priceResponse.getPrice();
//
//        assertEquals(price.getAsk(), CommonTestClassMembers.ASK);
//        assertEquals(price.getBid(), CommonTestClassMembers.BID);
//        assertTrue(price.isTradable());
//        assertEquals(price.getDateTime(),currentTime);
//    }

}
