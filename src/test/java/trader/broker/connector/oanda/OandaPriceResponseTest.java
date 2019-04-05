package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.OandaAPIMock.OandaAPIMockPricing;
import trader.connection.Connection;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Connection.class)
public class OandaPriceResponseTest {

    private static final String URL = "xxx.xxx";

    private Context contextMock;
    private Request requestMock;
    private OandaAPIMockPricing oandaAPIMockPricing;
    private OandaPriceResponse priceResponse;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMockPricing = new OandaAPIMockPricing();
        oandaAPIMockPricing.setMockPricingGetResponse(oandaAPIMockPricing.getMockPricingGetResponse());
        contextMock = oandaAPIMockPricing.getContext();
        requestMock = mock(Request.class);
        when(requestMock.getRequestDataStructure()).thenReturn(PricingGetRequest.class);
        priceResponse = new OandaPriceResponse();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallGetPriceResponseWithNullContext_Exception(){
        priceResponse.getPriceResponse(null, URL, requestMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallGetPriceResponseWithNullURL_Exception(){
        priceResponse.getPriceResponse(contextMock, null, requestMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallGetPriceResponseWithNullRequest_Exception(){
        priceResponse.getPriceResponse(contextMock, URL, null);
    }

    @Test
    public void WhenCallGetPriceResponseWithCorrectValues_ReturnCorrectResult(){
        PricingGetResponse actualResponse = this.priceResponse.getPriceResponse(contextMock, URL, requestMock);
        PricingGetResponse expectedResponse = oandaAPIMockPricing.getMockPricingGetResponse();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void WhenCallGetPriceResponseThrowExecuteOrRequestExceptions_WaitToReconnect() throws RequestException, ExecuteException {
        oandaAPIMockPricing.setMockPricingGetResponseToThrowException(RequestException.class);
        PowerMockito.mockStatic(Connection.class);
        PowerMockito.when(Connection.waitToConnect(URL)).thenReturn(true);
        PricingGetResponse actualResponse = this.priceResponse.getPriceResponse(contextMock, URL, requestMock);
        assertNull(actualResponse);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallGetPriceResponseThrowUnexpectedException_ThrowRuntimeException() throws RequestException, ExecuteException {
        oandaAPIMockPricing.setMockPricingGetResponseToThrowException(RuntimeException.class);
        this.priceResponse.getPriceResponse(contextMock, URL, requestMock);
    }
}
