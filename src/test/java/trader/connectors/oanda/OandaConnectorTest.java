package trader.connectors.oanda;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPI.OandaAPIMockAccount;
import trader.connectors.ApiConnector;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaConnectorTest {

    private CommonTestClassMembers commonMembers;
    private OandaConnector oandaConnector;
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaConfig mockOandaConfig;

    @Before
    public void setUp() {
        oandaConnector = (OandaConnector) ApiConnector.create("Oanda");
        commonMembers = new CommonTestClassMembers();
        oandaAPIMockAccount = new OandaAPIMockAccount();
        mockOandaConfig = mock(OandaConfig.class);

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
}
