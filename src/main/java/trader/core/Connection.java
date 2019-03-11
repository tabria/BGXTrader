package trader.core;
import trader.config.Config;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Provide methods for connectivity
 */
public final class Connection {

//TODO make method to check for exceptions 503 and 504 - no service for account

    private Connection() {
    }

    /**
     * Waiting till reconnect
     */
    public static void waitToConnect() {
        String host = Config.URL.substring(8);
       while (true){
           try {
               InetAddress[] allByName = InetAddress.getAllByName(host);
               break;
           } catch (UnknownHostException e) {
               try {
                   System.out.println("Connection lost. Reconnect in 30 sec ...");
                   Thread.sleep(30000);
               } catch (InterruptedException e1) {
                   e1.printStackTrace();
               }
           }
       }
    }


}
