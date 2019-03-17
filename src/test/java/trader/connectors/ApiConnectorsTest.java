package trader.connectors;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

import static trader.connectors.ApiConnectors.*;

public class ApiConnectorsTest {


    @Test (expected = NullArgumentException.class)
    public void createAPIConnectorWithNull_Exception(){
        ApiConnector apiConnector = ApiConnectors.create(null);
    }

    @Test
    public void createAPIConnector(){
        ApiConnector apiConnector = ApiConnectors.create("Oanda");
        String simpleName = apiConnector.getClass().getSimpleName();
        Assert.assertEquals("OandaConnector", simpleName);
    }

    @Test
    public void testConnectorClassNameComposition(){
        ApiConnector apiConnector = ApiConnectors.create("OANDA");
        String simpleName = apiConnector.getClass().getSimpleName();
        Assert.assertEquals("OandaConnector", simpleName);
    }

   @Test(expected = NoSuchConnectorException.class)
   public void createAPIConnectorWithNotExistingClassName(){
       ApiConnectors.create("KRAMBA");
   }
}
