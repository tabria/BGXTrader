package trader.controller;


import trader.exception.NullArgumentException;
import trader.requestor.*;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

public class CreateTradeController<T> implements TraderController<T> {

    private UseCaseFactory useCaseFactory;

    public CreateTradeController(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    public Response<T> execute(Map<String, Object> settings) {
        String controllerName = this.getClass().getSimpleName();
        Request<?> request = getRequest(controllerName, settings);
        UseCase useCase = make(controllerName);
        return useCase.execute(request);
    }

    Request<?> getRequest(String controllerName, Map<String, Object> settings) {
        RequestBuilder builder = RequestBuilderCreator.create(controllerName);
        return builder.build(settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }
}
