package trader.broker.connector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchGatewayException;
import trader.exception.NullArgumentException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseGatewayTest {

    private static final String OANDA = "Oanda";

    private BrokerConnector connectorMock;

    @Before
    public void setUp() throws Exception {
        connectorMock = mock(BrokerConnector.class);
        when(connectorMock.getUrl()).thenReturn("xxx.como");
    }

    @Test (expected = NullArgumentException.class)
    public void createBrokerGatewayWithNullName_Exception(){
        BaseGateway.create(null, connectorMock);
    }

    @Test (expected = NullArgumentException.class)
    public void createBrokerGatewayWithNullConnector_Exception(){
        BaseGateway.create(OANDA, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void createBrokerGatewayWithEmptyName_Exception(){
        BaseGateway.create(" ", connectorMock);
    }

    @Test
    public void createBrokerGatewayWithCorrectName(){
        BaseGateway baseGateway = BaseGateway.create(OANDA, connectorMock);
        String simpleName = baseGateway.getClass().getSimpleName();

        Assert.assertEquals("OandaGateway", simpleName);
    }

    @Test
    public void createBrokerGatewayWithCorrectNameWithExtraSpaces_CorrectResult(){
        BaseGateway baseGateway = BaseGateway.create("  Oanda    ", connectorMock);
        String simpleName = baseGateway.getClass().getSimpleName();

        Assert.assertEquals("OandaGateway", simpleName);
    }


   @Test(expected = NoSuchGatewayException.class)
   public void createBrokerGatewayWithNotExistingName(){
       BaseGateway.create("KRAMBA", connectorMock);
   }
}
