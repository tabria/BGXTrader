package trader.controller;

import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.RequestOLDBuilder;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

public class AddEntryStrategyController<T> implements TraderController<T> {

    private RequestOLDBuilder requestOLDBuilder;
    private UseCaseFactory useCaseFactory;

    public AddEntryStrategyController(RequestOLDBuilder requestOLDBuilder, UseCaseFactory useCaseFactory) {
        if(requestOLDBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestOLDBuilder = requestOLDBuilder;
        this.useCaseFactory = useCaseFactory;
    }


    public Response<T> execute(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName().trim();
        Request<?> request = requestOLDBuilder.build(controllerName, settings);
        UseCase useCase = useCaseFactory.make(controllerName);
        return useCase.execute(request);
    }
}
