package trader.connector.oanda;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.candlestick.Candlestick;
import trader.connector.BaseConnector;
import trader.price.Price;
import trader.price.Pricing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaConnectorTest {

    private CommonTestClassMembers commonMembers;
    private OandaConnector oandaConnector;
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaConfig mockOandaConfig;
    private OandaPriceResponse mockResponse;
    private OandaCandlesResponse mockCandlesResponse;
    private Pricing mockPrice;
    private Candlestick mockCandle;
    private List<Candlestick> candlesticks;


    @Before
    public void setUp() {
        oandaConnector = (OandaConnector) BaseConnector.create("Oanda");
        commonMembers = new CommonTestClassMembers();
        oandaAPIMockAccount = new OandaAPIMockAccount();
        mockOandaConfig = mock(OandaConfig.class);
        mockResponse = mock(OandaPriceResponse.class);
        mockCandlesResponse = mock(OandaCandlesResponse.class);
        mockPrice = mock(Price.class);
        mockCandle = mock(Candlestick.class);
        candlesticks = new ArrayList<>();
    }

    @Test
    public void getContextReturnCorrectContext(){
        commonMembers.changeFieldObject(oandaConnector, "context", oandaAPIMockAccount.getContext());
        assertEquals(oandaConnector.getContext(), oandaAPIMockAccount.getContext());
    }

    @Test
    public void getAccountIDReturnCorrectAccountID() {
        when(mockOandaConfig.getAccountID()).thenReturn(oandaAPIMockAccount.getMockAccountID());
        commonMembers.changeFieldObject(oandaConnector, "oandaConfig", mockOandaConfig);
        assertEquals(oandaConnector.getAccountID(), oandaAPIMockAccount.getMockAccountID());
    }

    @Test
    public void getUrlReturnCorrectUrl(){
        String expected = "yes.com";
        when(mockOandaConfig.getUrl()).thenReturn(expected);
        commonMembers.changeFieldObject(oandaConnector, "oandaConfig", mockOandaConfig);
        assertEquals(expected, oandaConnector.getUrl());
    }

    @Test
    public void getTokenReturnCorrectToken(){
        String expected = "GTSJSDSA-123XD";
        when(mockOandaConfig.getToken()).thenReturn(expected);
        commonMembers.changeFieldObject(oandaConnector, "oandaConfig", mockOandaConfig);
        assertEquals(expected, oandaConnector.getToken());
    }

    @Test
    public void testGetPrice(){
        commonMembers.changeFieldObject(oandaConnector, "oandaPriceResponse", mockResponse);
        when(oandaConnector.getPrice()).thenReturn(mockPrice);
        assertEquals(mockPrice, oandaConnector.getPrice());
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void getCorrectInitialCandlesQuantiy() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        setCandlesResponseField();
        List<Candlestick> initialCandlesList = (List<Candlestick>) invokeTestMethod("getInitialCandles");

        assertSame(candlesticks, initialCandlesList);
    }

    @Test
    public void getCorrectCandlesQuantityWhenUpdating() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        setCandlesResponseField();
        Candlestick candlestick = (Candlestick) invokeTestMethod("getUpdatedCandle");

        assertSame(mockCandle, candlestick);
    }

    @Test
    public void testGetUpdateCandle_CorrectResult(){
        setCandlesResponseField();
        assertEquals(mockCandle, oandaConnector.getUpdatedCandle());
    }


    private void setCandlesResponseField() {
        when(mockCandlesResponse.getUpdateCandle()).thenReturn(mockCandle);
        when(mockCandlesResponse.getInitialCandles()).thenReturn(candlesticks);
        commonMembers.changeFieldObject(oandaConnector, "oandaCandlesResponse", mockCandlesResponse);
    }

    private Object invokeTestMethod(String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getUpdateCandles = commonMembers.getPrivateMethodForTest(oandaConnector, methodName);
        return getUpdateCandles.invoke(oandaConnector);
    }

}
