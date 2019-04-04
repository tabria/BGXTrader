package trader.controller;

import trader.broker.BrokerConnector;
import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

public class AddBrokerConnectorController {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    public AddBrokerConnectorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }

    public Response<BrokerConnector> execute(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName().trim();
        Request<?> request = getRequest(controllerName, settings);
        UseCase useCase = make(controllerName);
        return useCase.execute(request);
    }

    Request<?> getRequest(String controllerName, HashMap<String, String> settings) {
        return requestBuilder.build(controllerName, settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }


}
