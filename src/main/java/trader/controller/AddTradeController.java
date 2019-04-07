package trader.controller;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;
import trader.responder.Response;

import java.util.Map;

public class AddTradeController<T> implements TraderController<T> {

//    private RequestBuilder requestBuilder;
//    private UseCaseFactory useCaseFactory;
    private BrokerGateway brokerGateway;
    private TradingStrategyConfiguration configuration;

    public AddTradeController(BrokerGateway brokerGateway, TradingStrategyConfiguration configuration) {
        if(brokerGateway == null || configuration == null)
            throw new NullArgumentException();
        this.brokerGateway = brokerGateway;
        this.configuration = configuration;
    }

//    public AddTradeController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, BrokerGateway brokerGateway) {
//        this.requestBuilder = requestBuilder;
//        this.useCaseFactory = useCaseFactory;
//        this.brokerGateway = brokerGateway;
//    }

    @Override
    public Response<T> execute(Map<String, Object> settings) {
        return null;
    }
}
