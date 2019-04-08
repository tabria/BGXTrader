package trader.observer;

import trader.broker.BrokerGateway;
import trader.exception.NullArgumentException;

public abstract class BaseObserver implements Observer {

    protected BrokerGateway brokerGateway;

    BaseObserver(BrokerGateway brokerGateway) {
        if(brokerGateway == null)
            throw new NullArgumentException();
        this.brokerGateway = brokerGateway;

    }
}
