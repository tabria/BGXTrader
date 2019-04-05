package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.pricing.PricingGetResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.OandaAPIMock.OandaAPIMockPricing;
import trader.connection.Connection;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Connection.class)
public class OandaResponseBuilderTest {

    private static final String URL = "xxx.xxx";

    private Context contextMock;
    private Request requestMock;
    private OandaAPIMockPricing oandaAPIMockPricing;
    private OandaAPIMockInstrument oandaAPIMockInstrument;
    private OandaResponseBuilder priceResponse;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMockPricing = new OandaAPIMockPricing();
        oandaAPIMockInstrument = new OandaAPIMockInstrument(2);
        oandaAPIMockPricing.setMockPricingGetResponse(oandaAPIMockPricing.getMockPricingGetResponse());
        contextMock = oandaAPIMockPricing.getContext();
        requestMock = mock(Request.class);
        when(requestMock.getRequestDataStructure()).thenReturn(oandaAPIMockPricing.getMockPricingGetRequest());
        priceResponse = new OandaResponseBuilder();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallGetPriceResponseWithNullType_Exception(){
        priceResponse.buildResponse(null,contextMock, URL, requestMock);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildResponseWithEmptyType_Exception(){
        priceResponse.buildResponse(" ",contextMock, URL, requestMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildResponseWithNullContext_Exception(){
        priceResponse.buildResponse("price", null, URL, requestMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildResponseWithNullURL_Exception(){
        priceResponse.buildResponse("price",contextMock, null, requestMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildResponseWithNullRequest_Exception(){
        priceResponse.buildResponse("price", contextMock, URL, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildResponseWithEmptyURL_Exception(){
        priceResponse.buildResponse("price", contextMock, " ", requestMock);
    }

    @Test
    public void WhenCallBuildResponseWithCorrectValues_ReturnCorrectResult(){
        Response<PricingGetResponse> actualResponse = this.priceResponse.buildResponse("price", contextMock, URL, requestMock);
        PricingGetResponse response = actualResponse.getResponseDataStructure();
        PricingGetResponse expectedResponse = oandaAPIMockPricing.getMockPricingGetResponse();

        assertEquals(expectedResponse, response);
    }

    @Test
    public void WhenBuildResponseThrowExecuteOrRequestExceptions_ResponseIsNull() throws RequestException, ExecuteException {
        oandaAPIMockPricing.setMockPricingGetResponseToThrowException(RequestException.class);
        PowerMockito.mockStatic(Connection.class);
        PowerMockito.when(Connection.waitToConnect(URL)).thenReturn(true);
        Response<PricingGetResponse> actualResponse = this.priceResponse.buildResponse("price", contextMock, URL, requestMock);
        assertNull(actualResponse);
    }

    @Test(expected = RuntimeException.class)
    public void WhenBuildResponseThrowUnexpectedException_ThrowRuntimeException() throws RequestException, ExecuteException {
        oandaAPIMockPricing.setMockPricingGetResponseToThrowException(RuntimeException.class);
        this.priceResponse.buildResponse("price", contextMock, URL, requestMock);
    }

    @Test
    public void WhenCallBuildResponseWithCandle_CorrectResponse(){
        when(requestMock.getRequestDataStructure()).thenReturn(oandaAPIMockInstrument.getMockRequest());
        Response response = this.priceResponse.buildResponse("candle", oandaAPIMockInstrument.getContext(), URL, requestMock);

        assertEquals(oandaAPIMockInstrument.getMockResponse(), response.getResponseDataStructure());
    }

    @Test
    public void WhenBuildResponseForCandleThrowExecuteOrRequestExceptions_ResponseIsNull() throws RequestException, ExecuteException {
        when(requestMock.getRequestDataStructure()).thenReturn(oandaAPIMockInstrument.getMockRequest());
        oandaAPIMockInstrument.setMockInstrumentCandlesResponseToThrowException(RequestException.class);
        PowerMockito.mockStatic(Connection.class);
        PowerMockito.when(Connection.waitToConnect(URL)).thenReturn(true);
        Response<InstrumentCandlesResponse> actualResponse = this.priceResponse.buildResponse("candle", oandaAPIMockInstrument.getContext(), URL, requestMock);
        assertNull(actualResponse);
    }
}
