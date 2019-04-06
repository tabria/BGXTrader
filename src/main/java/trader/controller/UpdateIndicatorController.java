package trader.controller;

import trader.broker.BrokerGateway;
import trader.exception.NullArgumentException;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.util.HashMap;

//to be removed

public class UpdateIndicatorController<T> implements TraderController<T> {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private TradingStrategyConfiguration tradingStrategyConfiguration;
    private BrokerGateway brokerGateway;

    public UpdateIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, TradingStrategyConfiguration configuration, BrokerGateway connector) {
        verifyInput(requestBuilder, useCaseFactory, configuration, connector);
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
        this.tradingStrategyConfiguration = configuration;
        this.brokerGateway = connector;
    }

    private void verifyInput(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, TradingStrategyConfiguration configuration, BrokerGateway connector) {
        if(requestBuilder == null || useCaseFactory == null ||
                configuration == null || connector == null)
            throw new NullArgumentException();
    }

    @Override
    public Response<T> execute(HashMap<String, String> settings) {
        return null;
    }
}
