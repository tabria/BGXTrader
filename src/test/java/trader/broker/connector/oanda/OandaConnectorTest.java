package trader.broker.connector.oanda;

import com.oanda.v20.pricing.PricingGetResponse;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.broker.connector.BaseConnector;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.price.Price;
import trader.price.PriceImpl;
import trader.requestor.Request;
import trader.responder.Response;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaConnectorTest {

    private CommonTestClassMembers commonMembers;
    private OandaConnector oandaConnector;
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaResponseBuilder mockResponse;
    private OandaRequestBuilder mockRequest;
    private OandaPriceTransformer mockPriceTransformer;
    private Price mockPrice;
    private Request requestMock;
    private PricingGetResponse pricingResponseMock;
    private Response responseMock;


    @Before
    public void setUp() {
        oandaConnector = (OandaConnector) BaseConnector.create("Oanda");
        commonMembers = new CommonTestClassMembers();
        oandaAPIMockAccount = new OandaAPIMockAccount();
        mockResponse = mock(OandaResponseBuilder.class);
        mockRequest = mock(OandaRequestBuilder.class);
        mockPriceTransformer = mock(OandaPriceTransformer.class);
        mockPrice = mock(PriceImpl.class);
        requestMock = mock(Request.class);
        pricingResponseMock = mock(PricingGetResponse.class);
        responseMock = mock(Response.class);
        when(responseMock.getResponseDataStructure()).thenReturn(pricingResponseMock);

    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetFileLocationWithNull_Exception(){
        oandaConnector.setFileLocation(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetFileLocationWithEmptyString_Exception(){
        oandaConnector.setFileLocation("");
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallSetFileLocationWithStringWithoutYamlOrYmlExtension_Exception(){
        oandaConnector.setFileLocation("gtre.exe");
    }

    @Test
    public void WhenCallSetFileLocationWithCorrectStringContainingExtraSpaces_TrimAndSet(){
        oandaConnector.setFileLocation("   sss.yaml ");

        assertEquals("sss.yaml", oandaConnector.getFileLocation());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetUrlWithNull_Exception(){
        oandaConnector.setUrl(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetUrlWithEmptyString_Exception(){
        oandaConnector.setUrl("");
    }

    @Test
    public void WhenCallSetUrlWithCorrectUrlContainingSpaces_TrimAndSet(){
        String url = "  https://www.www.con ";
        oandaConnector.setUrl(url);
        assertEquals(url.trim(),oandaConnector.getUrl());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetTokenWithNull_Exception(){
        oandaConnector.setToken(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetTokenWithEmptyString_Exception(){
        oandaConnector.setToken("");
    }

    @Test
    public void WhenCallSetTokenWithCorrectUrlContainingSpaces_TrimAndSet(){
        String token = "  dfsawewq32wsD!@#Dsa321sds1 ";
        oandaConnector.setToken(token);
        assertEquals(token.trim(),oandaConnector.getToken());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetAccountIDWithNull_Exception(){
        oandaConnector.setAccountID(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetAccountIDWithEmptyString_Exception(){
        oandaConnector.setAccountID("");
    }

    @Test
    public void WhenCallSetAccountIDWithCorrectUrlContainingSpaces_TrimAndSet(){
        String accountID = "  123-453-432-321 ";
        oandaConnector.setAccountID(accountID);
        assertEquals(accountID.trim(),oandaConnector.getAccountID());
    }

    @Test
    public void WhenCallValidateConnector_ContextMustNotBeNull(){
        setOandaValidator();
        oandaConnector.validateConnector();

        assertNotNull(oandaConnector.getContext());
    }

    @Test
    public void getContextReturnCorrectContext(){
        commonMembers.changeFieldObject(oandaConnector, "context", oandaAPIMockAccount.getContext());
        assertEquals(oandaConnector.getContext(), oandaAPIMockAccount.getContext());
    }

    @Test
    public void testGetPriceToReturnCorrectValue(){
        setFakePrice();
        Price actualPrice = oandaConnector.getPrice("EUR_USD");

        assertEquals(mockPrice, actualPrice);
    }

    private void setOandaValidator() {
        OandaAccountValidator mockValidator = mock(OandaAccountValidator.class);
        doNothing().when(mockValidator).validateAccount(oandaConnector);
        doNothing().when(mockValidator).validateAccountBalance(oandaConnector);
        commonMembers.changeFieldObject(oandaConnector, "oandaAccountValidator", mockValidator);
    }

    private void setFakePrice() {
        HashMap<String, String> settings = new HashMap<>();
        when(mockRequest.build(anyString(), any(HashMap.class))).thenReturn(requestMock);
        when(mockResponse.buildResponse(anyString(),eq(oandaAPIMockAccount.getContext()), anyString() ,eq(requestMock))).thenReturn(responseMock);
        when(mockPriceTransformer.transformToPrice(responseMock)).thenReturn(mockPrice);

        commonMembers.changeFieldObject(oandaConnector, "context", oandaAPIMockAccount.getContext());
        commonMembers.changeFieldObject(oandaConnector, "accountID", "ss");
        commonMembers.changeFieldObject(oandaConnector, "url", "dd");
        commonMembers.changeFieldObject(oandaConnector, "oandaRequestBuilder", mockRequest);
        commonMembers.changeFieldObject(oandaConnector, "oandaResponseBuilder", mockResponse);
        commonMembers.changeFieldObject(oandaConnector, "oandaPriceTransformer", mockPriceTransformer);
    }

//
//    @SuppressWarnings(value = "unchecked")
//    @Test
//    public void getCorrectInitialCandlesQuantiy() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        setCandlesResponseField();
//        List<Candlestick> initialCandlesList = (List<Candlestick>) invokeTestMethod("getInitialCandles");
//
//        assertSame(candlesticks, initialCandlesList);
//    }
//
//    @Test
//    public void getCorrectCandlesQuantityWhenUpdating() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        setCandlesResponseField();
//        Candlestick candlestick = (Candlestick) invokeTestMethod("updateCandle");
//
//        assertSame(mockCandle, candlestick);
//    }
//
//    @Test
//    public void testGetUpdateCandle_CorrectResult(){
//        setCandlesResponseField();
//        assertEquals(mockCandle, oandaConnector.updateCandle());
//    }
//
//
//    private void setCandlesResponseField() {
//        when(mockCandlesResponse.getUpdateCandle()).thenReturn(mockCandle);
//        when(mockCandlesResponse.getInitialCandles()).thenReturn(candlesticks);
//        commonMembers.changeFieldObject(oandaConnector, "oandaCandlesResponse", mockCandlesResponse);
//    }
//
//    private Object invokeTestMethod(String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        Method getUpdateCandles = commonMembers.getPrivateMethodForTest(oandaConnector, methodName);
//        return getUpdateCandles.invoke(oandaConnector);
//    }

}
