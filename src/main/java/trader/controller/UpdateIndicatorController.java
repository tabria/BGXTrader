package trader.controller;

import trader.broker.BrokerConnector;
import trader.exception.NullArgumentException;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.util.HashMap;

public class UpdateIndicatorController<T> implements TraderController<T> {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private TradingStrategyConfiguration tradingStrategyConfiguration;
    private BrokerConnector brokerConnector;

    public UpdateIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, TradingStrategyConfiguration configuration, BrokerConnector connector) {
        verifyInput(requestBuilder, useCaseFactory, configuration, connector);
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
        this.tradingStrategyConfiguration = configuration;
        this.brokerConnector = connector;
    }

    private void verifyInput(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, TradingStrategyConfiguration configuration, BrokerConnector connector) {
        if(requestBuilder == null || useCaseFactory == null ||
                configuration == null || connector == null)
            throw new NullArgumentException();
    }

    @Override
    public Response<T> execute(HashMap<String, String> settings) {
        return null;
    }
}
