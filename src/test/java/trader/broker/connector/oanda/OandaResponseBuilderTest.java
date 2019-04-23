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
import trader.OandaAPIMock.*;
import trader.connection.Connection;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.NullArgumentException;
import trader.presenter.Presenter;
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
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaResponseBuilder responseBuilder;
    private Presenter presenterMock;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws RequestException, ExecuteException {
        oandaAPIMockPricing = new OandaAPIMockPricing();
        oandaAPIMockInstrument = new OandaAPIMockInstrument(2);
        oandaAPIMockOrder = new OandaAPIMockOrder();
        oandaAPIMockTrade = new OandaAPIMockTrade();
        oandaAPIMockAccount = new OandaAPIMockAccount();
        oandaAPIMockPricing.setMockPricingGetResponse(oandaAPIMockPricing.getMockPricingGetResponse());
        contextMock = oandaAPIMockPricing.getContext();
        requestMock = mock(Request.class);
        presenterMock = mock(Presenter.class);
        commonMembers = new CommonTestClassMembers();
        when(requestMock.getBody()).thenReturn(oandaAPIMockPricing.getMockPricingGetRequest());
        responseBuilder = new OandaResponseBuilder(contextMock, URL, presenterMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedThenContextMustNotBeNull(){
        new OandaResponseBuilder(null, "url", presenterMock);
    }

    @Test
    public void WhenCreatedThenContextMusttBeWithCorrectValue(){
        Context context = (Context) commonMembers.extractFieldObject(responseBuilder, "context");

        assertEquals(contextMock, context);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullURL_Exception(){
        new OandaResponseBuilder(contextMock, null, presenterMock);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCreatedWithEmptyURL_Exception(){
        new OandaResponseBuilder(contextMock, "  ", presenterMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatedWithNullPresenter_Exception(){
        new OandaResponseBuilder(contextMock, "url", null);
    }

    @Test
    public void WhenCreatedWithCorrectURLWithExtraSpaces_CorrectResult(){
        String testUrl = "  xxx.com   ";
        OandaResponseBuilder oandaResponseBuilder = new OandaResponseBuilder(contextMock, testUrl, presenterMock);
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

    @Test(expected = NoSuchDataStructureException.class)
    public void WhenCallBuildResponseWithNotExistingType_ThrowException(){
        responseBuilder.buildResponse("kar", requestMock);
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
        setStaticConnectionWaitToReturnTrue();
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
        OandaResponseBuilder responseBuilder = new OandaResponseBuilder(oandaAPIMockInstrument.getContext(), URL, presenterMock);
        when(requestMock.getBody()).thenReturn(oandaAPIMockInstrument.getMockRequest());
        Response response = responseBuilder.buildResponse("candle", requestMock);

        assertEquals(oandaAPIMockInstrument.getMockResponse(), response.getBody());
    }

    @Test
    public void WhenBuildResponseForCandleThrowExecuteOrRequestExceptions_ResponseIsNull() throws RequestException, ExecuteException {
        setStaticConnectionWaitToReturnTrue();
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
        setStaticConnectionWaitToReturnTrue();
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
        setStaticConnectionWaitToReturnTrue();
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
    public void WhenBuildResponseForStopLossPriceResponseThrowExecuteException_ReturnNull() throws RequestException, ExecuteException {
        setStaticConnectionWaitToReturnTrue();
        setFakeTradeSetDependentRequest();
        oandaAPIMockTrade.setDependentOrderResponseToThrowException(RequestException.class);
        Response response = responseBuilder.buildResponse("setStopLossPrice", requestMock);

        assertNull(response);
    }

    @Test
    public void givenAccountID_WhenCallBuildResponse_ThenReturnCorrectResponse(){
        AccountID accountID = mock(AccountID.class);
        when(accountID.toString()).thenReturn("123243432");
        when(requestMock.getBody()).thenReturn(accountID);
        createFakeAccountCreateRequest();
        Response response = responseBuilder.buildResponse("accountID", requestMock);

        assertEquals(oandaAPIMockAccount.getMockAccount(), response.getBody());
    }

    @Test(expected = RuntimeException.class)
    public void givenBadAccountID_WhenCallBuildResponse_ThenThrowException() {
        AccountID accountID = mock(AccountID.class);
        when(accountID.toString()).thenReturn("123243432");
        when(requestMock.getBody()).thenReturn(accountID);
        responseBuilder.buildResponse("accountID", requestMock);

    }

    private void setFakeTradeSetDependentRequest() {
        responseBuilder = new OandaResponseBuilder(oandaAPIMockTrade.getContext(), URL, presenterMock);
        when(requestMock.getBody()).thenReturn(oandaAPIMockTrade.getTradeSetDependentOrdersRequestMock());
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

        responseBuilder = new OandaResponseBuilder(oandaAPIMockOrder.getContext(), URL, presenterMock);
        when(requestMock.getBody()).thenReturn(objects);
        oandaAPIMockOrder.setOrderCancelResponse(accountIDMock, orderSpecifierMock);
    }

    private void createFakeInstrumentCandleRequest() {
        responseBuilder = new OandaResponseBuilder(oandaAPIMockInstrument.getContext(), URL, presenterMock);
        when(requestMock.getBody()).thenReturn(oandaAPIMockInstrument.getMockRequest());
    }

    private void createFakeOrderCreateRequest() {
        responseBuilder = new OandaResponseBuilder(oandaAPIMockOrder.getContext(), URL, presenterMock);
        when(requestMock.getBody()).thenReturn(oandaAPIMockOrder.getMockOrderCreateRequest());
    }

    private void createFakeAccountCreateRequest() {
        responseBuilder = new OandaResponseBuilder(oandaAPIMockAccount.getContext(), URL, presenterMock);
       // when(requestMock.getBody()).thenReturn(oandaAPIMockOrder.getMockOrderCreateRequest());
    }

    private void setStaticConnectionWaitToReturnTrue() {
        PowerMockito.mockStatic(Connection.class);
        PowerMockito.when(Connection.waitToConnect(URL, presenterMock)).thenReturn(true);
    }
}
