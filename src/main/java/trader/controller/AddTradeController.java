package trader.controller;

import trader.broker.BrokerGateway;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.Map;

public class AddTradeController<T> implements TraderController<T> {

//    private RequestBuilder requestBuilder;
//    private UseCaseFactory useCaseFactory;
    private BrokerGateway brokerGateway;

    public AddTradeController(BrokerGateway brokerGateway) {

        this.brokerGateway = brokerGateway;
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
