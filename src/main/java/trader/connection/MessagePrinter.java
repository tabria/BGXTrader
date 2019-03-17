package trader.connection;

public class MessagePrinter {

    private MessagePrinter(){};

    public static String printReconnecting(String message) {
        if (message.isEmpty()){
            message = "Connection lost. Reconnecting .......";
            System.out.println(message);
        }
        return message;
    }

    public static void printConnected(String message) {
        if (!message.isEmpty()){
            System.out.println("Connected");
        }
    }

    public static void printMessage(String expected) {
        System.out.println(expected);
    }
}
