package trader.connectors;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPI.OandaAPIMock;
import trader.connectors.oanda.OandaConnector;
import static org.junit.Assert.*;

public class OandaConnectorTest {

    private CommonTestClassMembers commonMembers;
    private OandaConnector oandaConnector;
    private OandaAPIMock oandaAPIMock;

    @Before
    public void setUp() throws Exception {
        oandaConnector = (OandaConnector) ApiConnectors.create("Oanda");
        commonMembers = new CommonTestClassMembers();
        oandaAPIMock = new OandaAPIMock();
    }

    @Test
    public void getContextReturnCorrectContext(){
        commonMembers.changeFieldObject(oandaConnector, "context", oandaAPIMock.getContext());
        assertEquals(oandaConnector.getContext(), oandaAPIMock.getContext());
    }

    @Test
    public void getAccountIDReturnCorrectAccountID(){
        commonMembers.changeFieldObject(oandaConnector, "accountID", oandaAPIMock.getMockAccountID());
        assertEquals(oandaConnector.getAccountID(), oandaAPIMock.getMockAccountID());
    }

    @Test
    public void getUrlReturnCorrectUrl(){
        assertEquals(oandaConnector.getUrl(), "https://api-fxtrade.oanda.com");
    }
}
