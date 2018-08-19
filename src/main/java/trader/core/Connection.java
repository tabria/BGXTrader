package trader.core;
import trader.config.Config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Connection {


    private Connection() {
    }

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

        String debig = "";
    }


}
