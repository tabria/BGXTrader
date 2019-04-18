package trader.controller;

import trader.requestor.*;
import trader.responder.Response;
import java.util.Map;


public class CreateIndicatorController<T> implements TraderController<T> {

    private UseCaseFactory useCaseFactory;

    public CreateIndicatorController(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    @Override
    public Response<T> execute(Map<String, Object> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = make(controllerName);
        Request<?> request = getRequest(controllerName, settings);
        return useCase.execute(request);
    }

    Request<?> getRequest(String controllerName, Map<String, Object> settings) {
        RequestBuilder builder = RequestBuilderCreator.create(controllerName);
        return  builder.build(settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }

}
