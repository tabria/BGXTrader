package trader.broker.connector;

import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class BaseConnector implements BrokerConnector {

    private static final String CONNECTOR_LOCATION = "trader.broker.connector.";//oanda.";
    private static final String CONNECTOR_SUFFIX = "Connector";

    public static BaseConnector create(String connectorName){
        if(connectorName == null)
            throw new NullArgumentException();
        if(connectorName.trim().isEmpty())
            throw new EmptyArgumentException();
        return createInstance(connectorName);
    }

    private static BaseConnector createInstance(String connectorName) {
        try {
            Class<?> connectorClass = Class.forName(composeConnectorClassName(connectorName));
            Constructor<?> connectorConstructor = connectorClass.getDeclaredConstructor();
            connectorConstructor.setAccessible(true);
            return (BaseConnector) connectorConstructor.newInstance();

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new NoSuchConnectorException();
        }
    }

    private static String composeConnectorClassName(String connectorName) {
        connectorName = connectorName.toLowerCase().trim();
        connectorName = CONNECTOR_LOCATION + connectorName + "." + Character.toUpperCase(connectorName.charAt(0))
                + connectorName.substring(1) + CONNECTOR_SUFFIX;
        return connectorName;
    }

}
