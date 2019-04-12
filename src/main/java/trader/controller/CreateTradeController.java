package trader.controller;


import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

public class CreateTradeController<T> implements TraderController<T> {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;


    public CreateTradeController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }

    @Override
    public Response<T> execute(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = useCaseFactory.make(controllerName);
        Request<?> request = requestBuilder.build(controllerName, settings);
        return useCase.execute(request);
    }
}
