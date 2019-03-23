package trader.connector;

import org.junit.Assert;
import org.junit.Test;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;

public class BaseConnectorTest {


    @Test (expected = NullArgumentException.class)
    public void createAPIConnectorWithNull_Exception(){
        BaseConnector baseConnector = BaseConnector.create(null);
    }

    @Test
    public void createAPIConnector(){
        BaseConnector baseConnector = BaseConnector.create("Oanda");
        String simpleName = baseConnector.getClass().getSimpleName();
        Assert.assertEquals("OandaConnector", simpleName);
    }

    @Test
    public void testConnectorClassNameComposition(){
        BaseConnector baseConnector = BaseConnector.create("OANDA");
        String simpleName = baseConnector.getClass().getSimpleName();
        Assert.assertEquals("OandaConnector", simpleName);
    }

   @Test(expected = NoSuchConnectorException.class)
   public void createAPIConnectorWithNotExistingClassName(){
       BaseConnector.create("KRAMBA");
   }
}
