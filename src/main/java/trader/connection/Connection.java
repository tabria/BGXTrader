package trader.connection;

import com.sun.javafx.binding.StringFormatter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connection {

    private static String message = "";

    private Connection() {
    }

    public static void waitToConnect(String url) {
        String host = url.substring(8);
        message ="";
        while (true){
           try {
               getAddresses(host);
               break;
           } catch (UnknownHostException e) {
               activateSleep();
           }
       }
        MessagePrinter.printMessage("Connected");
    }

    private static void getAddresses(String host) throws UnknownHostException {
        InetAddress[] allByName = InetAddress.getAllByName(host);
    }

    private static void activateSleep() {
        try {
            message = MessagePrinter.printReconnecting(message);
            Thread.sleep(30000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
