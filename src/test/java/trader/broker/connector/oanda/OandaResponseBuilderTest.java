package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.OrderSpecifier;
import com.oanda.v20.pricing.PricingGetResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.OandaAPIMock.OandaAPIMockOrder;
import trader.OandaAPIMock.OandaAPIMockPricing;
import trader.OandaAPIMock.OandaAPIMockTrade;
import trader.connection.Connection;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;

import java.util.ArrayList;
import java.util.List;

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
    private OandaAPIMockOrder oandaAPIMockOrder;
    private OandaAPIMockTrade oandaAPIMockTrade;
    private OandaResponseBuilder responseBuilder;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMockPricing = new OandaAPIMockPricing();
        oandaAPIMockInstrument = new OandaAPIMockInstrument(2);
        oandaAPIMockOrder = new OandaAPIMockOrder();
        oandaAPIMockTrade = new OandaAPIMockTrade();
        oandaAPIMockPricing.setMockPricingGetResponse(oandaAPIMockPricing.getMockPricingGetResponse());
        contextMock = oandaAPIMockPricing.getContext();
        requestMock = mock(Request.class);
        commonMembers = new CommonTestClassMembers();
        when(requestMock.getbody()).thenReturn(oandaAPIMockPricing.getMockPricingGetRequest());
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
        PricingGetResponse response = actualResponse.getBody();
        PricingGetResponse expectedResponse = oandaAPIMockPricing.getMockPricingGetResponse();

        assertEquals(expectedResponse, response);
    }

    @Test
    public void WhenBuildResponseThrowExecuteOrRequestExceptions_ResponseIsNull() throws RequestException, ExecuteException {
        oandaAPIMockPricing.setMockPricingGetResponseToThrowException(RequestException.class);
        setStaticConnectonWaitToReturnTrue();
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
        when(requestMock.getbody()).thenReturn(oandaAPIMockInstrument.getMockRequest());
        Response response = responseBuilder.buildResponse("candle", requestMock);

        assertEquals(oandaAPIMockInstrument.getMockResponse(), response.getBody());
    }

    @Test
    public void WhenBuildResponseForCandleThrowExecuteOrRequestExceptions_ResponseIsNull() throws RequestException, ExecuteException {
        setStaticConnectonWaitToReturnTrue();
        setInstrumentCandleResponseToThrowException(RequestException.class);
        Response<InstrumentCandlesResponse> actualResponse = responseBuilder.buildResponse("candle", requestMock);

        assertNull(actualResponse);
    }

    @Test(expected = RuntimeException.class)
    public void WhenBuildResponseWithCreateCandlesResponseThrowUnexpectedException_ThrowRuntimeException() throws RequestException, ExecuteException {
        setInstrumentCandleResponseToThrowException(RuntimeException.class);
        responseBuilder.buildResponse("candle", requestMock);
    }


    @Test
    public void WhenCallBuildResponseWithMarketIfTouchedOrder_CorrectResponse(){
        createFakeOrderCreateRequest();
        Response response = responseBuilder.buildResponse("marketIfTouchedOrder", requestMock);

        assertEquals(oandaAPIMockOrder.getMockOrderCreateResponse(), response.getBody());
    }

    @Test(expected = RuntimeException.class)
    public void WhenBuildResponseForCreateOrderCreateResponseThrowUnexpectedException_ThrowRuntimeException() throws RequestException, ExecuteException {
        setOrderCreateResponseToThrowException(RuntimeException.class);
        responseBuilder.buildResponse("marketIfTouchedOrder", requestMock);
    }

    @Test
    public void WhenBuildResponseForCreateOrderCreateResponseThrowExecuteException_ReturnNull() throws RequestException, ExecuteException {
        setStaticConnectonWaitToReturnTrue();
        setOrderCreateResponseToThrowException(RequestException.class);
        Response response = responseBuilder.buildResponse("marketIfTouchedOrder", requestMock);

        assertNull(response);
    }

    @Test
    public void WhenCallBuildResponseWithResponseForCancelOrderResponse_CorrectResponse(){
        createFakeOrderCancelRequest();
        Response response = responseBuilder.buildResponse("cancelOrder", requestMock);

        assertEquals(oandaAPIMockOrder.getMockOrderCancelResponse(), response.getBody());
    }


    @Test(expected = RuntimeException.class)
    public void WhenBuildResponseForResponseForCancelOrderResponseThrowUnexpectedException_ThrowRuntimeException() throws RequestException, ExecuteException {
        createFakeOrderCancelRequest();
        oandaAPIMockOrder.setMockOrderCancelResponseToThrowException(RuntimeException.class);
        responseBuilder.buildResponse("cancelOrder", requestMock);
    }

    @Test
    public void WhenBuildResponseForCancelOrderResponseThrowExecuteException_ReturnNull() throws RequestException, ExecuteException {
        setStaticConnectonWaitToReturnTrue();
        createFakeOrderCancelRequest();
        oandaAPIMockOrder.setMockOrderCancelResponseToThrowException(RequestException.class);
        Response response = responseBuilder.buildResponse("cancelOrder", requestMock);

        assertNull(response);
    }

    @Test
    public void WhenCallBuildResponseWithResponseForSetStopLossPriceResponse_CorrectResponse(){
        setFakeTradeSetDependentRequest();
        Response response = responseBuilder.buildResponse("setStopLossPrice", requestMock);

        assertEquals(oandaAPIMockTrade.getTradeSetDependentOrdersResponseMock(), response.getBody());
    }

    @Test(expected = RuntimeException.class)
    public void WhenBuildResponseForResponseForCetStopLossPriceResponseThrowUnexpectedException_ThrowRuntimeException() throws RequestException, ExecuteException {
        setFakeTradeSetDependentRequest();
        oandaAPIMockTrade.setDependentOrderResponseToThrowException(RuntimeException.class);
        responseBuilder.buildResponse("setStopLossPrice", requestMock);
    }

    @Test
    public void WhenBuildResponseForetStopLossPriceResponseThrowExecuteException_ReturnNull() throws RequestException, ExecuteException {
        setStaticConnectonWaitToReturnTrue();
        setFakeTradeSetDependentRequest();
        oandaAPIMockTrade.setDependentOrderResponseToThrowException(RequestException.class);
        Response response = responseBuilder.buildResponse("setStopLossPrice", requestMock);

        assertNull(response);
    }



    private void setFakeTradeSetDependentRequest() {
        responseBuilder = new OandaResponseBuilder(oandaAPIMockTrade.getContext(), URL);
        when(requestMock.getbody()).thenReturn(oandaAPIMockTrade.getTradeSetDependentOrdersRequestMock());
        oandaAPIMockTrade.setSetDependentOrdersMock();
    }

    private <T extends Throwable> void setOrderCreateResponseToThrowException(Class<T> exception) throws ExecuteException, RequestException {
        createFakeOrderCreateRequest();
        oandaAPIMockOrder.setMockOrderCreateResponseToThrowException(exception);
    }

    private <T extends Throwable> void setInstrumentCandleResponseToThrowException(Class<T> exception) throws ExecuteException, RequestException {
        createFakeInstrumentCandleRequest();
        oandaAPIMockInstrument.setMockInstrumentCandlesResponseToThrowException(exception);
    }

    private void createFakeOrderCancelRequest() {
        List<Object> objects = new ArrayList<>();
        AccountID accountIDMock = mock(AccountID.class);
        OrderSpecifier orderSpecifierMock = mock(OrderSpecifier.class);
        objects.add(accountIDMock);
        objects.add(orderSpecifierMock);

        responseBuilder = new OandaResponseBuilder(oandaAPIMockOrder.getContext(), URL);
        when(requestMock.getbody()).thenReturn(objects);
        oandaAPIMockOrder.setOrderCancelResponse(accountIDMock, orderSpecifierMock);
    }

    private void createFakeInstrumentCandleRequest() {
        responseBuilder = new OandaResponseBuilder(oandaAPIMockInstrument.getContext(), URL);
        when(requestMock.getbody()).thenReturn(oandaAPIMockInstrument.getMockRequest());
    }

    private void createFakeOrderCreateRequest() {
        responseBuilder = new OandaResponseBuilder(oandaAPIMockOrder.getContext(), URL);
        when(requestMock.getbody()).thenReturn(oandaAPIMockOrder.getMockOrderCreateRequest());
    }


    private void setStaticConnectonWaitToReturnTrue() {
        PowerMockito.mockStatic(Connection.class);
        PowerMockito.when(Connection.waitToConnect(URL)).thenReturn(true);
    }
}
