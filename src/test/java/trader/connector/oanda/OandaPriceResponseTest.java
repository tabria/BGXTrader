package trader.connector.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.PricingGetRequest;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.OandaAPIMock.OandaAPIMockPricing;
import trader.price.Pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OandaPriceResponseTest {

    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");
    private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
            .setScale(5, RoundingMode.HALF_UP);

    private OandaAPIMockPricing oandaAPIMockPricing;
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaPriceResponse priceResponse;
    private CommonTestClassMembers commonMembers;
    private OandaConnector oandaConnector;
    private OandaConnector mockOandaConnector;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMockPricing = new OandaAPIMockPricing();
        oandaAPIMockAccount = new OandaAPIMockAccount();
        oandaConnector = (OandaConnector) oandaConnector.create("Oanda");
        mockOandaConnector = mock(OandaConnector.class);
        oandaAPIMockPricing.setMockPricingGetResponse(oandaAPIMockPricing.getMockPricingGetResponse());
        priceResponse = new OandaPriceResponse(oandaConnector);
        commonMembers = new CommonTestClassMembers();
    }

    @Test
    public void getCorrectPriceRequest(){
        commonMembers.changeFieldObject(priceResponse, "oandaConnector", mockOandaConnector);
        when(mockOandaConnector.getAccountID()).thenReturn(oandaAPIMockAccount.getMockAccountID());
        priceResponse = new OandaPriceResponse(mockOandaConnector);
        PricingGetRequest priceRequest = (PricingGetRequest) commonMembers.extractFieldObject(priceResponse, "pricingGetRequest");

        assertTrue(priceRequest.getPathParams().containsValue(oandaAPIMockAccount.getMockAccountID()));
    }

    @Test
    public void getPriceReturnsCorrectValues(){
        ZonedDateTime currentTime = ZonedDateTime.now();
        Pricing price = priceResponse.getPrice();

        assertNotEquals(price.getAsk(), DEFAULT_ASK);
        assertNotEquals(price.getBid(), DEFAULT_BID);
//        assertTrue(price.isTradable());
        assertNotEquals(price.getDateTime(),DEFAULT_DATE_TIME);
        assertNotEquals(price.getAvailableUnits(), BigDecimal.ZERO);
    }

    @Test
    public void testGetPriceForNullResponse_tradableFalse() throws RequestException, ExecuteException {
        priceResponse = new OandaPriceResponse(mockOandaConnector);
        when(mockOandaConnector.getContext()).thenReturn(oandaAPIMockPricing.getMockContext());
        commonMembers.changeFieldObject(priceResponse, "pricingGetRequest", oandaAPIMockPricing.getMockPricingGetRequest());
        oandaAPIMockPricing.setMockPricingGetResponse(null);
        Pricing price = priceResponse.getPrice();

        assertEquals(price.getAsk(), DEFAULT_ASK);
        assertEquals(price.getBid(), DEFAULT_BID);
        assertFalse(price.isTradable());
        assertEquals(price.getDateTime(),DEFAULT_DATE_TIME);
        assertEquals(price.getAvailableUnits(), BigDecimal.ZERO);
    }

    @Test
    public void testGetPriceForNullClientPrice_tradableFalse() throws RequestException, ExecuteException {
        priceResponse = new OandaPriceResponse(mockOandaConnector);
        when(mockOandaConnector.getContext()).thenReturn(oandaAPIMockPricing.getMockContext());
        commonMembers.changeFieldObject(priceResponse, "pricingGetRequest", oandaAPIMockPricing.getMockPricingGetRequest());
        Pricing price = priceResponse.getPrice();

        assertEquals(price.getAsk(), DEFAULT_ASK);
        assertEquals(price.getBid(), DEFAULT_BID);
        assertFalse(price.isTradable());
        assertEquals(price.getDateTime(),DEFAULT_DATE_TIME);
        assertEquals(price.getAvailableUnits(), BigDecimal.ZERO);
    }

}
