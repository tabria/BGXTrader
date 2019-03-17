package trader.connection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "trader.connection.*")
public class ConnectionTest {

    private final String goodUrl = "https://api-fxtrade.oanda.com";
    private final String badUrl = "https://api-fxtrade.oanda.com";

    private ByteArrayOutputStream outContent;

    @Before
    public void setUp() throws Exception {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testWithCorrectURL(){
        String expected = "Connected";
        Connection.waitToConnect(goodUrl);

        Assert.assertEquals(expected,outContent.toString().trim());
    }


    @Test
    public void testPrintReConnected() throws Exception {


//        PowerMockito.mockStatic(ConnectionWrapper.class);
//
//        String a ="";
//        PowerMockito.doNothing().when(Connection.class, "getAddresses");
//        Connection.waitToConnect(badUrl);

//                PowerMockito.spy(Util.class);
//                PowerMockito.doReturn("abc").when(Util.class, "anotherMethod");

//                String retrieved = Util.method();
//
//                Assert.assertNotNull(retrieved);
//                Assert.assertEquals(retrieved, "abc");


//
//        Method getAddresses = Connection.class.getDeclaredMethod("getAddresses", String.class);
//        getAddresses.setAccessible(true);
//        PowerMockito.when(getAddresses.invoke(null, badUrl)).thenReturn(1);
//
//        doNothing().when(getAddresses).invoke(null);
//        doNothing().when(mockConnection).getClass().getDeclaredMethod("getAddresses", String.class).invoke(null);
//        //getAddresses.invoke(null);
//        Connection.waitToConnect(badUrl);

        //invokeVoidMethod("printReconnecting");

//        assertEquals(expected, outContent.toString().trim());
//        assertEquals(expected, actual);
    }

    //    private void invokeVoidMethod(String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Method printConnected = Connection.class.getDeclaredMethod(methodName);
//        printConnected.setAccessible(true);
//        printConnected.invoke(null);
//    }
//
//    private void changeMessage(String newMessage) throws NoSuchFieldException, IllegalAccessException {
//        Field message = Connection.class.getDeclaredField("message");
//        message.setAccessible(true);
//        message.set(null, newMessage);
//    }

}
