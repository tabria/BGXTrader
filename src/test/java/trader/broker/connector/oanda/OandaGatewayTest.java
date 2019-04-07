package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.trade.TradeSummary;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.entity.candlestick.Candlestick;
import trader.price.Price;
import trader.price.PriceImpl;
import trader.requestor.Request;
import trader.responder.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OandaGatewayTest {

    private static final String URL = "xxx.com";
    private CommonTestClassMembers commonMembers;
    private OandaGateway oandaGateway;
    private Context contextMock;
    private Price mockPrice;
    private Response responseMock;
    private BrokerConnector connectorMock;


    @Before
    public void setUp() {
        commonMembers = new CommonTestClassMembers();
        contextMock = mock(Context.class);
        mockPrice = mock(PriceImpl.class);
        responseMock = mock(Response.class);
        connectorMock = mock(BrokerConnector.class);
        when(connectorMock.getUrl()).thenReturn(URL);
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
        setFakeContext();
        assertEquals(oandaGateway.getContext(), contextMock);
    }

    @Test
    public void testGetPriceToReturnCorrectValue(){
        setFakePrice();
        Price actualPrice = oandaGateway.getPrice("EUR_USD");

        assertEquals(mockPrice, actualPrice);
    }

    @Test
    public void testGetCandlesToReturnCorrectValue(){
        List<Candlestick> candles = new ArrayList<>();
        HashMap<String, String> settings = new HashMap<>();
        setFakeContext();
        setFakeBuilders();
        setFakeCandlestickList(candles);
        List<Candlestick> actualCandles = oandaGateway.getCandles(settings);

        assertSame(candles, actualCandles);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void WhenCreatedThenPriceSettingsMustNotBeNull(){
        HashMap<String, String> priceSettings = (HashMap<String, String>) commonMembers.extractFieldObject(oandaGateway, "priceSettings");

        assertNotNull(priceSettings);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void WhenCreatedThenAccountSettingsMustNotBeNull(){
        HashMap<String, String> accountSettings = (HashMap<String, String>) commonMembers.extractFieldObject(oandaGateway, "accountSettings");

        assertNotNull(accountSettings);
    }

    @Test
    public void WhenCallTotalTradesSize_CorrectReturnValue(){
        List<TradeSummary> trades = new ArrayList<>();
        setFakeContext();
        setFakeBuilders();
        setFakeTradeSummary(trades);

        assertEquals(0,  oandaGateway.totalTradesSize());
    }

    private void setFakeTradeSummary(List<TradeSummary> trades) {
        Account accountMock = mock(Account.class);
        when(responseMock.getResponseDataStructure()).thenReturn(accountMock);
        when(accountMock.getTrades()).thenReturn(trades);
    }

    private void setFakeContext() {
        commonMembers.changeFieldObject(oandaGateway, "context", contextMock);
    }

    private void setOandaValidator() {
        OandaAccountValidator mockValidator = mock(OandaAccountValidator.class);
        doNothing().when(mockValidator).validateAccount(connectorMock, contextMock);
        doNothing().when(mockValidator).validateAccountBalance(connectorMock, contextMock);
        commonMembers.changeFieldObject(oandaGateway, "oandaAccountValidator", mockValidator);
    }

    @SuppressWarnings("unchecked")
    private void setFakePrice() {
        setFakeContext();
        setFakeBuilders();
        setFakePriceTransformer();
        when(connectorMock.getUrl()).thenReturn("dd");
    }

    @SuppressWarnings("unchecked")
    private void setFakePriceTransformer() {
        OandaPriceTransformer mockPriceTransformer = mockPriceTransformer = mock(OandaPriceTransformer.class);
        commonMembers.changeFieldObject(oandaGateway, "oandaPriceTransformer", mockPriceTransformer);
        when(mockPriceTransformer.transformToPrice(responseMock)).thenReturn(mockPrice);
    }

    @SuppressWarnings("unchecked")
    private void setFakeCandlestickList(List<Candlestick> candles) {
        OandaCandleTransformer candleTransformerMock = mock(OandaCandleTransformer.class);
        commonMembers.changeFieldObject(oandaGateway, "oandaCandlesTransformer", candleTransformerMock);
        when(candleTransformerMock.transformCandlesticks(responseMock)).thenReturn(candles);
    }

    @SuppressWarnings("unchecked")
    private void setFakeBuilders() {
        OandaResponseBuilder mockResponseBuilder = mock(OandaResponseBuilder.class);
        OandaRequestBuilder mockRequestBuilder = mock(OandaRequestBuilder.class);
        Request requestMock = mock(Request.class);
        commonMembers.changeFieldObject(oandaGateway, "oandaRequestBuilder", mockRequestBuilder);
        commonMembers.changeFieldObject(oandaGateway, "oandaResponseBuilder", mockResponseBuilder);
        when(mockRequestBuilder.build(anyString(), any(HashMap.class))).thenReturn(requestMock);
        when(mockResponseBuilder.buildResponse(anyString(), eq(requestMock))).thenReturn(responseMock);
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
