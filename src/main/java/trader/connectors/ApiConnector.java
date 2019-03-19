package trader.connectors;


import trader.candle.Candlestick;
import trader.exceptions.NoSuchConnectorException;
import trader.exceptions.NullArgumentException;
import trader.prices.Pricing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class ApiConnector {

    private static final String CONNECTOR_LOCATION = "trader.connectors.oanda.";
    private static final String CONNECTOR_SUFFIX = "Connector";

    public static ApiConnector create(String apiName){
        if(apiName == null){
            throw new NullArgumentException();
        }
        return createInstance(apiName);
    }

    public abstract Pricing getPrice();

    public abstract List<Candlestick> getInitialCandles();

    public abstract Candlestick getUpdateCandle();

    private static ApiConnector createInstance(String apiName) {
        try {
            Class<?> connectorClass = Class.forName(composeConnectorClassName(apiName));
            Constructor<?> connectorConstructor = connectorClass.getDeclaredConstructor();
            connectorConstructor.setAccessible(true);
            return (ApiConnector) connectorConstructor.newInstance();

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
