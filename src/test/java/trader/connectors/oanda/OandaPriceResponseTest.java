package trader.connectors.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.pricing.PricingGetRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import trader.CommonTestClassMembers;
import trader.OandaAPI.OandaAPIMock;
import trader.config.Config;
import trader.connectors.ApiConnector;
import trader.prices.Price;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class OandaPriceResponseTest {

    OandaAPIMock oandaAPIMock;
    OandaPriceResponse priceResponse;
    CommonTestClassMembers commonMembers;
    OandaConnector mockOandaConnector;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMock = new OandaAPIMock();
        mockOandaConnector = mock(OandaConnector.class);
        oandaAPIMock.setMockPricingGetResponse();
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
