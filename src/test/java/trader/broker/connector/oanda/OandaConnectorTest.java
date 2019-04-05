package trader.broker.connector.oanda;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.entity.candlestick.Candlestick;
import trader.broker.connector.BaseConnector;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.price.Price;
import trader.price.Pricing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
        commonMembers.changeFieldObject(oandaConnector, "oandaPriceResponse", mockResponse);
        when(oandaConnector.getPrice(anyString())).thenReturn(mockPrice);
        assertEquals(mockPrice, oandaConnector.getPrice("EUR_USD"));
    }

    private void setOandaValidator() {
        OandaAccountValidator mockValidator = mock(OandaAccountValidator.class);
        doNothing().when(mockValidator).validateAccount(oandaConnector);
        doNothing().when(mockValidator).validateAccountBalance(oandaConnector);
        commonMembers.changeFieldObject(oandaConnector, "oandaAccountValidator", mockValidator);
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
