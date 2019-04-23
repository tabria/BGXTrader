package trader.connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.CommonTestClassMembers;
import trader.presenter.Presenter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Thread.class)
public class ConnectionTest {

    private final String goodUrl = "https://api-fxtrade.oanda.com";
    private final String badUrl = "https://no.com";

    private PrintStream standardOut = System.out;
    private ByteArrayOutputStream outContent;
    private CommonTestClassMembers commonMembers;
    private Presenter presenterMock;

    @Before
    public void setUp() throws Exception {
        commonMembers = new CommonTestClassMembers();
        outContent = new ByteArrayOutputStream();
        presenterMock = mock(Presenter.class);
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown(){
        System.setOut(standardOut);
        System.out.println(outContent.toString());
    }

    @Test
    public void givenEmptyMessage_WhenCallActivateSleep_WhenPrintCorrectMessage() {
        setFakePresenter();
        ArgumentCaptor<String> argCapture = ArgumentCaptor.forClass(String.class);
        Connection.activateSleep("", 1L, presenterMock);

        verify(presenterMock, times(1)).execute(anyString());
        verify(presenterMock).execute(argCapture.capture());

        assertEquals(argCapture.getValue(), "Connection Lost.Reconnecting...");
    }

    @Test
    public void givenMessageHasValueConnected_WhenCallActivateSleep_WhenPrintCorrectMessage() {
        setFakePresenter();
        ArgumentCaptor<String> argCapture = ArgumentCaptor.forClass(String.class);
        Connection.activateSleep("connected", 1L, presenterMock);

        verify(presenterMock, times(1)).execute(anyString());
        verify(presenterMock).execute(argCapture.capture());

        assertEquals(argCapture.getValue(), "Connection Lost.Reconnecting...");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateSleepWithNegativeSleep(){
        setFakePresenter();
        PowerMockito.mockStatic(Thread.class);
        Connection.activateSleep("", -1L, presenterMock);
    }

    @Test
    public void testVerifyHostIpExistence() {
        setFakePresenter();
        boolean expectedGoodURL = Connection.verifyHostIpExistence(goodUrl, 1L, presenterMock);
        boolean expectedBadURL = Connection.verifyHostIpExistence(badUrl, 1L, presenterMock);

        assertTrue(expectedGoodURL);
        assertFalse(expectedBadURL);
    }

    @Test
    public void testExtractHost()  {
        setFakePresenter();;
        String badUrlExtracted = Connection.extractHost(badUrl);

        assertEquals("no.com", badUrlExtracted);
    }

    private void setFakePresenter(){
        doNothing().when(presenterMock).execute(anyString());
    }

    @Test
    public void givenCorrectUrl_WhenCallWaitToConnect_ThenPrintCorrectResult(){
        setFakePresenter();
        ArgumentCaptor<String> argCapture = ArgumentCaptor.forClass(String.class);

        Connection.waitToConnect(goodUrl, presenterMock);

        verify(presenterMock, timeout(1)).execute(anyString());
        verify(presenterMock).execute(argCapture.capture());

        assertEquals("CONNECTED", argCapture.getValue());
    }
}
