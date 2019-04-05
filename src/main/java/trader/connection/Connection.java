package trader.connection;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connection {

    private static final long DEFAULT_SLEEP_TIME_MILLIS = 30000;
    private static final int BEGIN_INDEX = 8;
    private static String message = "";

   // private Connection() { }

    public static boolean waitToConnect(String url) {
        while (true){
            if (verifyHostIpExistence(url, DEFAULT_SLEEP_TIME_MILLIS))
                break;
        }
        MessagePrinter.printMessage("Connected");
        return true;
    }

    private static boolean verifyHostIpExistence (String url, long sleepInterval) {
        message ="";
        try {
            getAddresses(extractHost(url));
            return true;
        } catch (UnknownHostException e) {
            activateSleep(sleepInterval);
        }
        return false;
    }

    private static String extractHost(String url) {
        return url.substring(BEGIN_INDEX);
    }

    private static void getAddresses(String host) throws UnknownHostException {
        InetAddress[] allByName = InetAddress.getAllByName(host);
    }

    private static void activateSleep(long sleepInterval) {
        try {
            message = MessagePrinter.printReconnecting(message);
            Thread.sleep(sleepInterval);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
