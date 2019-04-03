package trader.controller;

import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.bgxstrategy.configuration.BGXConfiguration;

import java.util.HashMap;

public class BGXConfigurationController {


    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    public BGXConfigurationController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }

    public Response<BGXConfiguration> execute(String useCaseName, HashMap<String, String> settings) {
        Request<?> request = getRequest(useCaseName, settings);
        UseCase useCase = make(useCaseName);
        return useCase.execute(request);
    }

    Request<?> getRequest(String type, HashMap<String, String> settings) {
        return requestBuilder.build(type, settings);
    }

    UseCase make(String useCaseName){
        return useCaseFactory.make(useCaseName);
    }
}
