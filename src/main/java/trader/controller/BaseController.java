package trader.controller;

import trader.requestor.*;
import trader.responder.Response;

import java.util.Map;

public abstract class BaseController<T>  implements TraderController<T> {
    protected UseCaseFactory useCaseFactory;

    public BaseController(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    public Response execute(Map<String, Object> settings) {
        String controllerName = this.getClass().getSimpleName().trim();
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
