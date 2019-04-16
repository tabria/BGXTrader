package trader.controller;


import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.RequestOLDBuilder;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

public class CreateTradeController<T> implements TraderController<T> {

    private RequestOLDBuilder requestOLDBuilder;
    private UseCaseFactory useCaseFactory;


    public CreateTradeController(RequestOLDBuilder requestOLDBuilder, UseCaseFactory useCaseFactory) {
        if(requestOLDBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestOLDBuilder = requestOLDBuilder;
        this.useCaseFactory = useCaseFactory;
    }


    public Response<T> execute(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = useCaseFactory.make(controllerName);
        Request<?> request = requestOLDBuilder.build(controllerName, settings);
        return useCase.execute(request);
    }
}
