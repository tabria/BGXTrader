package trader.controller;
import trader.exception.NullArgumentException;
import trader.requestor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import java.util.HashMap;


public class CreateIndicatorController<T> implements TraderController<T> {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    public CreateIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }


    public Response<T> execute(HashMap<String, String> settings) {
        return getIndicatorResponse(settings);
    }

    Request<?> getRequest(String controllerName, HashMap<String, String> settings){
        return requestBuilder.build(controllerName, settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }

    private Response<T> getIndicatorResponse(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = make(controllerName);
        Request<?> request = getRequest(controllerName, settings);
        return useCase.execute(request);
    }

}