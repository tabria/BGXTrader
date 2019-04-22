package trader.presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.broker.connector.oanda.OandaGateway;
import trader.configuration.BGXConfigurationImpl;
import trader.configuration.TradingStrategyConfiguration;
import trader.entry.EntryStrategy;
import trader.entry.standard.StandardEntryStrategy;
import trader.exit.ExitStrategy;
import trader.exit.halfclosetrail.HalfCloseTrailExitStrategy;
import trader.order.OrderStrategy;
import trader.order.standard.StandardOrderStrategy;
import trader.responder.Response;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

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
        String header = getHeader();
        String text = "Open";
        when(responseMock.getBody()).thenReturn(text);
        consolePresenter.execute(responseMock);

        assertEquals(header+" " +text, consoleContent.toString().trim());
    }

    @Test
    public void givenCorrectConfigurationSettings_WhenCallExecute_ThenPrintSuccessfulConfiguration(){
        TradingStrategyConfiguration config = mock(BGXConfigurationImpl.class);
        when(config.toString()).thenCallRealMethod();
        when(responseMock.getBody()).thenReturn(config);
        String expected = String.format("%s %s", getHeader(), "BGXConfiguration");

        consolePresenter.execute(responseMock);

        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenCorrectBrokerGatewaySettings_WhenCallExecute_ThenPrintSuccessfulBrokerGatewayCreation(){
        BrokerGateway gateway = mock(OandaGateway.class);
        when(gateway.toString()).thenCallRealMethod();
        when(responseMock.getBody()).thenReturn(gateway);
        String expected = String.format("%s %s", getHeader(), "Gateway: OANDA");

        consolePresenter.execute(responseMock);

        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenCorrectEntryStrategySettings_WhenCallExecute_ThenPrintSuccessfulEntryStrategyCreation(){
        EntryStrategy entryStrategy = mock(StandardEntryStrategy.class);
        when(entryStrategy.toString()).thenCallRealMethod();
        when(responseMock.getBody()).thenReturn(entryStrategy);
        String expected = String.format("%s %s", getHeader(), "Entry strategy: STANDARD");

        consolePresenter.execute(responseMock);

        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenCorrectOrderStrategySettings_WhenCallExecute_ThenPrintSuccessfulOrderStrategyCreation(){
        OrderStrategy orderStrategy = mock(StandardOrderStrategy.class);
        when(orderStrategy.toString()).thenCallRealMethod();
        when(responseMock.getBody()).thenReturn(orderStrategy);
        String expected = String.format("%s %s", getHeader(), "Order strategy: STANDARD");

        consolePresenter.execute(responseMock);

        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenCorrectExitStrategySettings_WhenCallExecute_ThenPrintSuccessfulExitStrategyCreation(){
        ExitStrategy orderStrategy = mock(HalfCloseTrailExitStrategy.class);
        when(orderStrategy.toString()).thenCallRealMethod();
        when(responseMock.getBody()).thenReturn(orderStrategy);
        String expected = String.format("%s %s", getHeader(), "Exit strategy: HALF CLOSE, TRAIL");

        consolePresenter.execute(responseMock);

        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenMultipleStrings_WhenCallExecute_ThenPrintCorrectMessage(){
        consolePresenter.execute("1", "position closed @", "1.2345");
        String expected = String.format("%s %s %s %s", getHeader(), "Full", "position closed @", "1.2345");
        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenMultipleStringsWithPartsToCloseAboveOne_WhenCallExecute_ThenPrintCorrectMessage(){
        consolePresenter.execute("3", "position closed @", "1.2345");
        String expected = String.format("%s %s %s %s", getHeader(), "1/3", "position closed @", "1.2345");
        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenMultipleStringsWithNewOrder_WhenCallExecute_ThenPrintCorrectMessage(){
        String trade = "UP trade entry@1.2345, SL@1.12344 units:";
        consolePresenter.execute("EUR_USD", trade, "123");
        String expected = String.format("%s %s %s %s", getHeader(), "[EUR_USD]", trade, "123");
        assertEquals(expected, consoleContent.toString().trim());
    }

    @Test
    public void givenSingleString_WhenCallExecute_ThenPrintCorrectMessage(){
        consolePresenter.execute("move to break even");
        String expected = String.format("%s %s", getHeader(), "move to break even");
        assertEquals(expected, consoleContent.toString().trim());
    }


    private String getHeader() {
        long timeMillis = System.currentTimeMillis();
        Date date = new Date(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("---- EEE MMM dd yyyy HH:mm:ss ----");

        String trader = "TRADER";
        return String.format("[%s] %s", trader, simpleDateFormat.format(date));
    }

}
