package trader.connection;
import trader.presenter.Presenter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connection {

    private static final long DEFAULT_SLEEP_TIME_MILLIS = 30000;
    private static final int BEGIN_INDEX = 8;
    private static String message = "";

    public static boolean waitToConnect(String url, Presenter presenter) {
        while (true){
            if (verifyHostIpExistence(url, DEFAULT_SLEEP_TIME_MILLIS, presenter))
                break;
        }
        presenter.execute("CONNECTED");
        return true;
    }

    static boolean verifyHostIpExistence (String url, long sleepInterval, Presenter presenter) {
        try {
            getAddresses(extractHost(url));
            return true;
        } catch (UnknownHostException e) {
            activateSleep(message, sleepInterval, presenter);
        }
        return false;
    }

    static String extractHost(String url) {
        return url.substring(BEGIN_INDEX);
    }

    private static void getAddresses(String host) throws UnknownHostException {
        InetAddress[] allByName = InetAddress.getAllByName(host);
    }

    static void activateSleep(String message, long sleepInterval, Presenter presenter) {
        try {
            if(message.isEmpty() || message.equalsIgnoreCase("connected")){
                setMessage("Connection Lost.Reconnecting...");
                presenter.execute(getMessage());
            }
            Thread.sleep(sleepInterval);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private static void setMessage(String inputMessage){
        message = inputMessage;
    }

    private static String getMessage() {
        return message;
    }
}
