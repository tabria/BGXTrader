package trader.broker.connector;

import trader.broker.BrokerGateway;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchGatewayException;
import trader.exception.NullArgumentException;
import trader.presenter.Presenter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class BaseGateway implements BrokerGateway {

    private static final String CONNECTOR_LOCATION = "trader.broker.connector.";//oanda.";
    private static final String CONNECTOR_SUFFIX = "Gateway";

    public static BaseGateway create(String gatewayName, BrokerConnector brokerConnector, Presenter presenter){
        if(gatewayName == null || brokerConnector == null || presenter == null)
            throw new NullArgumentException();
        if(gatewayName.trim().isEmpty())
            throw new EmptyArgumentException();
        return createInstance(gatewayName, brokerConnector, presenter);
    }

    private static BaseGateway createInstance(String gatewayName, BrokerConnector brokerConnector, Presenter presenter) {
        try {
            Class<?> gatewayClass = Class.forName(composeGatewayClassName(gatewayName));
            Constructor<?> gatewayConstructor = gatewayClass.getDeclaredConstructor(BrokerConnector.class, Presenter.class);
            gatewayConstructor.setAccessible(true);
            return (BaseGateway) gatewayConstructor.newInstance(brokerConnector, presenter);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new NoSuchGatewayException();
        }
    }

    private static String composeGatewayClassName(String gatewayName) {
        gatewayName = gatewayName.toLowerCase().trim();
        gatewayName = CONNECTOR_LOCATION + gatewayName + "." + Character.toUpperCase(gatewayName.charAt(0))
                + gatewayName.substring(1) + CONNECTOR_SUFFIX;
        return gatewayName;
    }

}
