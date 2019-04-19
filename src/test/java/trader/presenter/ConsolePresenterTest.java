package trader.presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import trader.responder.Response;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ConsolePresenterTest {

    private PrintStream standardOut = System.out;
    private ByteArrayOutputStream consoleContent;
    private Response responseMock;
    private ConsolePresenter consolePresenter;

    @Before
    public void setUp() throws Exception {
        responseMock = mock(Response.class);
        consoleContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(consoleContent));
        consolePresenter = new ConsolePresenter();
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(standardOut);
        System.out.println(consoleContent.toString());
    }

    @Test
    public void givenInput_WhenCallExecute_ThenPrintHeader(){
        long timeMillis = System.currentTimeMillis();
        Date date = new Date(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" --- EEE MMM dd yyyy HH:mm:ss --- ");
        String text = "Open";
        String trader = "TRADER";
        String header = String.format("[%s] %s ", trader, simpleDateFormat.format(date));
        consolePresenter.execute(responseMock);
        assertEquals(header+text, consoleContent.toString().trim());
        String a = "";
    }

}
