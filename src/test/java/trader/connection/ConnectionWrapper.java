package trader.connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConnectionWrapper {

    private static String message = "";


    public void waitToConnectWrapper(String url){
        Connection.waitToConnect(url);
    }


    public static void getAddressesWrapper(String host) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getAddresses = Connection.class.getDeclaredMethod("getAddresses", String.class);
        getAddresses.setAccessible(true);
        getAddresses.invoke(null, host);
    }

    public static void activateSleepWrapper() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getAddresses = Connection.class.getDeclaredMethod("activateSleep");
        getAddresses.setAccessible(true);
        getAddresses.invoke(null);
    }
}
