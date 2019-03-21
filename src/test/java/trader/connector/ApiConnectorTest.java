package trader.connector;

import org.junit.Assert;
import org.junit.Test;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;

public class ApiConnectorTest {


    @Test (expected = NullArgumentException.class)
    public void createAPIConnectorWithNull_Exception(){
        ApiConnector apiConnector = ApiConnector.create(null);
    }

    @Test
    public void createAPIConnector(){
        ApiConnector apiConnector = ApiConnector.create("Oanda");
        String simpleName = apiConnector.getClass().getSimpleName();
        Assert.assertEquals("OandaConnector", simpleName);
    }

    @Test
    public void testConnectorClassNameComposition(){
        ApiConnector apiConnector = ApiConnector.create("OANDA");
        String simpleName = apiConnector.getClass().getSimpleName();
        Assert.assertEquals("OandaConnector", simpleName);
    }

   @Test(expected = NoSuchConnectorException.class)
   public void createAPIConnectorWithNotExistingClassName(){
       ApiConnector.create("KRAMBA");
   }
}
