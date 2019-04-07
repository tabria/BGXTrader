package trader.observer;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;

public abstract class BaseObserver implements Observer {

    protected final TradingStrategyConfiguration configuration;
    protected BrokerGateway brokerGateway;

    BaseObserver(TradingStrategyConfiguration configuration, BrokerGateway brokerGateway) {
        if(configuration == null || brokerGateway == null)
            throw new NullArgumentException();
        this.brokerGateway = brokerGateway;
        this.configuration = configuration;
    }
}
