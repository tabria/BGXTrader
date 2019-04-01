package trader.controller;

import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;
public class AddIndicatorController implements UseCaseController {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;


    public AddIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }

    @Override
    public <T> Response<T> execute(String indicatorType, HashMap<String, String> settings){
        UseCase useCase = make(composeUseCaseName());
        Request<?> request = requestBuilder.build(indicatorType, settings);
        return useCase.execute(request);
    }

    Request<?> getRequest(String indicatorType, HashMap<String, String> settings){
        if(indicatorType == null || settings == null)
            throw new NullArgumentException();
        return requestBuilder.build(indicatorType, settings);
    }

    UseCase make(String useCaseName){
        if(useCaseName == null)
            throw new NullArgumentException();
        return useCaseFactory.make(useCaseName);
    }

    String composeUseCaseName(){
        String controllerName = this.getClass().getSimpleName();
        return controllerName.replace("Controller", "UseCase").trim();
    }

}
