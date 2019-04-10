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
import trader.CommonTestClassMembers;
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
    private OandaResponseBuilder responseBuilder;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMockPricing = new OandaAPIMockPricing();
        oandaAPIMockInstrument = new OandaAPIMockInstrument(2);
        oandaAPIMockPricing.setMockPricingGetResponse(oandaAPIMockPricing.getMockPricingGetResponse());
        contextMock = oandaAPIMockPricing.getContext();
        requestMock = mock(Request.class);
        commonMembers = new CommonTestClassMembers();
        when(requestMock.getRequestDataStructure()).thenReturn(oandaAPIMockPricing.getMockPricingGetRequest());
        responseBuilder = new OandaResponseBuilder(contextMock, URL);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedThenContextMustNotBeNull(){
        new OandaResponseBuilder(null, "url");
    }

    @Test
    public void WhenCreatedThenContextMusttBeWithCorrectValue(){
        Context context = (Context) commonMembers.extractFieldObject(responseBuilder, "context");

        assertEquals(contextMock, context);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullURL_Exception(){
        new OandaResponseBuilder(contextMock, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCreatedWithEmptyURL_Exception(){
        new OandaResponseBuilder(contextMock, "  ");
    }

    @Test
    public void WhenCreatedWithCorrectURLWithExtraSpaces_CorrectResult(){
        String testUrl = "  xxx.com   ";
        OandaResponseBuilder oandaResponseBuilder = new OandaResponseBuilder(contextMock, testUrl );
        String url = (String) commonMembers.extractFieldObject(oandaResponseBuilder, "url");

        assertEquals(testUrl.trim(), url);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallGetPriceResponseWithNullType_Exception(){
        responseBuilder.buildResponse(null, requestMock);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildResponseWithEmptyType_Exception(){
        responseBuilder.buildResponse(" ", requestMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildResponseWithNullRequest_Exception(){
        responseBuilder.buildResponse("price", null);
    }

    @Test
    public void WhenCallBuildResponseWithNotExistingType_ReturnNull(){
        Response response = responseBuilder.buildResponse("kar", requestMock);

        assertNull(response);
    }

    @Test
    public void WhenCallBuildResponseWithCorrectValues_ReturnCorrectResult(){
        Response<PricingGetResponse> actualResponse = this.responseBuilder.buildResponse("price", requestMock);
        PricingGetResponse response = actualResponse.getResponseDataStructure();
        PricingGetResponse expectedResponse = oandaAPIMockPricing.getMockPricingGetResponse();

        assertEquals(expectedResponse, response);
    }

    @Test
    public void WhenBuildResponseThrowExecuteOrRequestExceptions_ResponseIsNull() throws RequestException, ExecuteException {
        oandaAPIMockPricing.setMockPricingGetResponseToThrowException(RequestException.class);
        PowerMockito.mockStatic(Connection.class);
        PowerMockito.when(Connection.waitToConnect(URL)).thenReturn(true);
        Response<PricingGetResponse> actualResponse = this.responseBuilder.buildResponse("price", requestMock);
        assertNull(actualResponse);
    }

    @Test(expected = RuntimeException.class)
    public void WhenBuildResponseThrowUnexpectedException_ThrowRuntimeException() throws RequestException, ExecuteException {
        oandaAPIMockPricing.setMockPricingGetResponseToThrowException(RuntimeException.class);
        this.responseBuilder.buildResponse("price", requestMock);
    }

    @Test
    public void WhenCallBuildResponseWithCandle_CorrectResponse(){
        OandaResponseBuilder responseBuilder = new OandaResponseBuilder(oandaAPIMockInstrument.getContext(), URL);
        when(requestMock.getRequestDataStructure()).thenReturn(oandaAPIMockInstrument.getMockRequest());
        Response response = responseBuilder.buildResponse("candle", requestMock);

        assertEquals(oandaAPIMockInstrument.getMockResponse(), response.getResponseDataStructure());
    }

    @Test
    public void WhenBuildResponseForCandleThrowExecuteOrRequestExceptions_ResponseIsNull() throws RequestException, ExecuteException {
        OandaResponseBuilder responseBuilder = new OandaResponseBuilder(oandaAPIMockInstrument.getContext(), URL);
        when(requestMock.getRequestDataStructure()).thenReturn(oandaAPIMockInstrument.getMockRequest());
        oandaAPIMockInstrument.setMockInstrumentCandlesResponseToThrowException(RequestException.class);
        PowerMockito.mockStatic(Connection.class);
        PowerMockito.when(Connection.waitToConnect(URL)).thenReturn(true);
        Response<InstrumentCandlesResponse> actualResponse = responseBuilder.buildResponse("candle", requestMock);

        assertNull(actualResponse);
    }
}
