package trader.connectors;

import com.oanda.v20.RequestException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connection {

    private Connection() {
    }

    public static void waitToConnect(String url) {
        String host = url.substring(8);
        String message ="";
        while (true){
           try {
               InetAddress[] allByName = InetAddress.getAllByName(host);
               break;
           } catch (UnknownHostException e) {
               try {
                   if (message.equals("")){
                       message = "Connection lost. Reconnecting .......";
                       System.out.println(message);
                   }
                   Thread.sleep(30000);
               } catch (InterruptedException e1) {
                   e1.printStackTrace();
               }
           }
       }
    }
}
