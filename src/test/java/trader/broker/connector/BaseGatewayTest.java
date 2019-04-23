package trader.broker.connector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchGatewayException;
import trader.exception.NullArgumentException;
import trader.presenter.Presenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseGatewayTest {

    private static final String OANDA = "Oanda";

    private BrokerConnector connectorMock;
    private Presenter presenterMock;

    @Before
    public void setUp() throws Exception {
        presenterMock = mock(Presenter.class);
        connectorMock = mock(BrokerConnector.class);
        when(connectorMock.getUrl()).thenReturn("xxx.como");
    }

    @Test (expected = NullArgumentException.class)
    public void createBrokerGatewayWithNullName_Exception(){
        BaseGateway.create(null, connectorMock, presenterMock);
    }

    @Test (expected = NullArgumentException.class)
    public void createBrokerGatewayWithNullConnector_Exception(){
        BaseGateway.create(OANDA, null, presenterMock);
    }

    @Test(expected = EmptyArgumentException.class)
    public void createBrokerGatewayWithEmptyName_Exception(){
        BaseGateway.create(" ", connectorMock, presenterMock);
    }

    @Test
    public void createBrokerGatewayWithCorrectName(){
        BaseGateway baseGateway = BaseGateway.create(OANDA, connectorMock, presenterMock);
        String simpleName = baseGateway.getClass().getSimpleName();

        Assert.assertEquals("OandaGateway", simpleName);
    }

    @Test
    public void createBrokerGatewayWithCorrectNameWithExtraSpaces_CorrectResult(){
        BaseGateway baseGateway = BaseGateway.create("  Oanda    ", connectorMock, presenterMock);
        String simpleName = baseGateway.getClass().getSimpleName();

        Assert.assertEquals("OandaGateway", simpleName);
    }


   @Test(expected = NoSuchGatewayException.class)
   public void createBrokerGatewayWithNotExistingName(){
       BaseGateway.create("KRAMBA", connectorMock, presenterMock);
   }
}
