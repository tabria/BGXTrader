package trader.controller;

import trader.requestor.*;
import trader.responder.Response;
import java.util.Map;

public class CreateBGXConfigurationController<T> implements TraderController<T> {

    private UseCaseFactory useCaseFactory;

    public CreateBGXConfigurationController(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    @Override
    public Response<T> execute(Map<String, Object> settings) {
        String controllerName = this.getClass().getSimpleName().trim();
        Request request = getRequest(controllerName, settings);
        UseCase useCase = make(controllerName);
        return useCase.execute(request);
    }

    Request getRequest(String controllerName, Map<String, Object> settings) {
        RequestBuilder factory = RequestBuilderCreator.create(controllerName);
        return  factory.build(settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }
}
