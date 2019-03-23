package trader.connector;

import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class BaseConnector implements ApiConnector {

    private static final String CONNECTOR_LOCATION = "trader.connector.oanda.";
    private static final String CONNECTOR_SUFFIX = "Connector";

    public static BaseConnector create(String apiName){
        if(apiName == null){
            throw new NullArgumentException();
        }
        return createInstance(apiName);
    }

    private static BaseConnector createInstance(String apiName) {
        try {
            Class<?> connectorClass = Class.forName(composeConnectorClassName(apiName));
            Constructor<?> connectorConstructor = connectorClass.getDeclaredConstructor();
            connectorConstructor.setAccessible(true);
            return (BaseConnector) connectorConstructor.newInstance();

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new NoSuchConnectorException();
        }
    }

    private static String composeConnectorClassName(String apiName) {
        apiName = apiName.toLowerCase();
        apiName = CONNECTOR_LOCATION + Character.toUpperCase(apiName.charAt(0))
                + apiName.substring(1) + CONNECTOR_SUFFIX;
        return apiName;
    }

}
