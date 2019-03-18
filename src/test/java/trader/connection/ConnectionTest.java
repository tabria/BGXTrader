package trader.connection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;


public class ConnectionTest {

    private final String goodUrl = "https://api-fxtrade.oanda.com";
    private final String badUrl = "https://no.com";

    private ByteArrayOutputStream outContent;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {
        commonMembers = new CommonTestClassMembers();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }


    @Test
    public void testActivateSleep() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long expected = 1234L;
        Method activateSleep = commonMembers.getPrivateMethodForTest(Connection.class, "activateSleep", long.class);
        long startTime = System.currentTimeMillis();
        activateSleep.invoke(null, expected);
        long endTime = System.currentTimeMillis();

        assertTrue(expected <= endTime - startTime);
    }

    @Test(expected = InvocationTargetException.class)
    public void testActivateSleepWithNegativeSleep() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long expected = -1L;
        Method activateSleep = commonMembers.getPrivateMethodForTest(Connection.class, "activateSleep", long.class);
        activateSleep.invoke(null, expected);
    }

    @Test
    public void testVerifyHostIpExistence() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method verifyIp = commonMembers.getPrivateMethodForTest(Connection.class, "verifyHostIpExistence", String.class, long.class);
        boolean expectedGoodURL = (boolean) verifyIp.invoke(null, goodUrl, 1L);
        boolean expectedBadURL = (boolean) verifyIp.invoke(null, badUrl, 1L);

        assertTrue(expectedGoodURL);
        assertFalse(expectedBadURL);
    }

    @Test
    public void testExtractHost() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method extractHost = commonMembers.getPrivateMethodForTest(Connection.class, "extractHost", String.class);
        String actual = (String) extractHost.invoke(null, badUrl);

        assertEquals("no.com", actual);
    }
}
