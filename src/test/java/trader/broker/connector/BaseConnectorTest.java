package trader.broker.connector;

import org.junit.Assert;;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;


public class BaseConnectorTest {

    private static final String OANDA = "Oanda";


    @Test(expected = NullArgumentException.class)
    public void createBrokerConnectorWithNullName_Exception(){
        BaseConnector.create(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void createBrokerConnectorWithEmptyName_Exception(){
        BaseConnector.create(" ");
    }

    @Test
    public void createBrokerConnectorWithCorrectName(){
        BrokerConnector baseConnector = BaseConnector.create(OANDA);
        String simpleName = baseConnector.getClass().getSimpleName();

        Assert.assertEquals("OandaConnector", simpleName);
    }

    @Test
    public void createBrokerConnectorWithCorrectNameWithExtraSpaces_CorrectResult(){
        BrokerConnector baseConnector = BaseConnector.create("  Oanda    ");
        String simpleName = baseConnector.getClass().getSimpleName();

        Assert.assertEquals("OandaConnector", simpleName);
    }


    @Test(expected = NoSuchConnectorException.class)
    public void createBrokerConnectorWithNotExistingName(){
        BaseConnector.create("KRAMBA");
    }

}
