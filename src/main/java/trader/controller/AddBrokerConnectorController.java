package trader.controller;

import trader.exception.NullArgumentException;
import trader.requestor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestOLDBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

public class AddBrokerConnectorController<T> implements TraderController<T> {

    private RequestOLDBuilder requestOLDBuilder;
    private UseCaseFactory useCaseFactory;

    public AddBrokerConnectorController(RequestOLDBuilder requestOLDBuilder, UseCaseFactory useCaseFactory) {
        if(requestOLDBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestOLDBuilder = requestOLDBuilder;
        this.useCaseFactory = useCaseFactory;
    }

    public Response<T> execute(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName().trim();
        Request<?> request = getRequest(controllerName, settings);
        UseCase useCase = make(controllerName);
        return useCase.execute(request);
    }

    Request<?> getRequest(String controllerName, HashMap<String, String> settings) {
        return requestOLDBuilder.build(controllerName, settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }


}
