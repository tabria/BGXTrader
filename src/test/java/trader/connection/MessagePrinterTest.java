package trader.connection;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MessagePrinterTest {

    private String url = "https://api-fxtrade.oanda.com";
    private ByteArrayOutputStream outContent;

    @Before
    public void setUp() throws Exception {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testPrintConnected() {
        String expected = "Connected";
        MessagePrinter.printConnected("Not Empty");

        assertEquals(expected, outContent.toString().trim());
    }

    @Test
    public void testPrintReConnected() {
        String expected = "Connection lost. Reconnecting .......";
        String actual = MessagePrinter.printReconnecting("");

        assertEquals(expected, outContent.toString().trim());
        assertEquals(expected, actual);
    }

    @Test
    public void testPrintConnectionMessage(){
        String expected = "Some message";
        MessagePrinter.printMessage(expected);

        assertEquals(expected, outContent.toString().trim());
    }
}
