package trader.controller;

import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

public class AddEntryStrategyController<T> implements TraderController<T> {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    public AddEntryStrategyController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }

    @Override
    public Response<T> execute(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName().trim();
        Request<?> request = requestBuilder.build(controllerName, settings);
        UseCase useCase = useCaseFactory.make(controllerName);
        return useCase.execute(request);
    }
}
