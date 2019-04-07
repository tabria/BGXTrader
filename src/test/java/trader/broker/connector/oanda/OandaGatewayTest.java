package trader.broker.connector.oanda;

import com.oanda.v20.pricing.PricingGetResponse;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.price.Price;
import trader.price.PriceImpl;
import trader.requestor.Request;
import trader.responder.Response;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaGatewayTest {

    private CommonTestClassMembers commonMembers;
    private OandaGateway oandaGateway;
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaResponseBuilder mockResponse;
    private OandaRequestBuilder mockRequest;
    private OandaPriceTransformer mockPriceTransformer;
    private Price mockPrice;
    private Request requestMock;
    private PricingGetResponse pricingResponseMock;
    private Response responseMock;
    private BrokerConnector connectorMock;


    @Before
    public void setUp() {
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
        connectorMock = mock(BrokerConnector.class);
        oandaGateway = (OandaGateway) BaseGateway.create("Oanda", connectorMock);
    }

    @Test
    public void WhenCallValidateConnector_ContextMustNotBeNull(){
        setOandaValidator();
        oandaGateway.validateConnector();

        assertNotNull(oandaGateway.getContext());
    }

    @Test
    public void getContextReturnCorrectContext(){
        commonMembers.changeFieldObject(oandaGateway, "context", oandaAPIMockAccount.getContext());
        assertEquals(oandaGateway.getContext(), oandaAPIMockAccount.getContext());
    }

    @Test
    public void testGetPriceToReturnCorrectValue(){
        setFakePrice();
        Price actualPrice = oandaGateway.getPrice("EUR_USD");

        assertEquals(mockPrice, actualPrice);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void WhenCreatedThenPriceSettingsMustNotBeNull(){
        HashMap<String, String> priceSettings = (HashMap<String, String>) commonMembers.extractFieldObject(oandaGateway, "priceSettings");

        assertNotNull(priceSettings);
    }

    private void setOandaValidator() {
        OandaAccountValidator mockValidator = mock(OandaAccountValidator.class);
        doNothing().when(mockValidator).validateAccount(connectorMock, oandaAPIMockAccount.getContext());
        doNothing().when(mockValidator).validateAccountBalance(connectorMock, oandaAPIMockAccount.getContext());
        commonMembers.changeFieldObject(oandaGateway, "oandaAccountValidator", mockValidator);
    }

    @SuppressWarnings("unchecked")
    private void setFakePrice() {
        HashMap<String, String> settings = new HashMap<>();
        when(mockRequest.build(anyString(), any(HashMap.class))).thenReturn(requestMock);
        when(mockResponse.buildResponse(anyString(),eq(oandaAPIMockAccount.getContext()), anyString() ,eq(requestMock))).thenReturn(responseMock);
        when(mockPriceTransformer.transformToPrice(responseMock)).thenReturn(mockPrice);
        when(connectorMock.getUrl()).thenReturn("dd");

        commonMembers.changeFieldObject(oandaGateway, "context", oandaAPIMockAccount.getContext());
        commonMembers.changeFieldObject(oandaGateway, "oandaRequestBuilder", mockRequest);
        commonMembers.changeFieldObject(oandaGateway, "oandaResponseBuilder", mockResponse);
        commonMembers.changeFieldObject(oandaGateway, "oandaPriceTransformer", mockPriceTransformer);
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
//        assertEquals(mockCandle, oandaGateway.updateCandle());
//    }
//
//
//    private void setCandlesResponseField() {
//        when(mockCandlesResponse.getUpdateCandle()).thenReturn(mockCandle);
//        when(mockCandlesResponse.getInitialCandles()).thenReturn(candlesticks);
//        commonMembers.changeFieldObject(oandaGateway, "oandaCandlesResponse", mockCandlesResponse);
//    }
//
//    private Object invokeTestMethod(String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        Method getUpdateCandles = commonMembers.getPrivateMethodForTest(oandaGateway, methodName);
//        return getUpdateCandles.invoke(oandaGateway);
//    }

}
