package trader.broker.connector.oanda;

import org.junit.Before;
import org.junit.Test;
import trader.broker.connector.BaseConnector;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;

import static org.junit.Assert.assertEquals;

public class OandaConnectorTest {

    private BaseConnector connector;

    @Before
    public void setUp() throws Exception {
        connector = BaseConnector.create("Oanda");
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetFileLocationWithNull_Exception(){
        connector.setFileLocation(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetFileLocationWithEmptyString_Exception(){
        connector.setFileLocation("");
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallSetFileLocationWithStringWithoutYamlOrYmlExtension_Exception(){
        connector.setFileLocation("gtre.exe");
    }

    @Test
    public void WhenCallSetFileLocationWithCorrectStringContainingExtraSpaces_TrimAndSet(){
        connector.setFileLocation("   sss.yaml ");

        assertEquals("sss.yaml", connector.getFileLocation());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetUrlWithNull_Exception(){
        connector.setUrl(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetUrlWithEmptyString_Exception(){
        connector.setUrl("");
    }

    @Test
    public void WhenCallSetUrlWithCorrectUrlContainingSpaces_TrimAndSet(){
        String url = "  https://www.www.con ";
        connector.setUrl(url);
        assertEquals(url.trim(), connector.getUrl());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetTokenWithNull_Exception(){
        connector.setToken(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetTokenWithEmptyString_Exception(){
        connector.setToken("");
    }

    @Test
    public void WhenCallSetTokenWithCorrectUrlContainingSpaces_TrimAndSet(){
        String token = "  dfsawewq32wsD!@#Dsa321sds1 ";
        connector.setToken(token);
        assertEquals(token.trim(), connector.getToken());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetAccountIDWithNull_Exception(){
        connector.setAccountID(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetAccountIDWithEmptyString_Exception(){
        connector.setAccountID("");
    }

    @Test
    public void WhenCallSetAccountIDWithCorrectUrlContainingSpaces_TrimAndSet(){
        String accountID = "  123-453-432-321 ";
        connector.setAccountID(accountID);
        assertEquals(accountID.trim(), connector.getAccountID());
    }

}
