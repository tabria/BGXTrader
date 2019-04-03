package trader.controller;

import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.Observable;

import java.util.HashMap;
public class AddIndicatorController{

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    public AddIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }

    public void execute(HashMap<String, String> settings, Observable priceObservable){
        if(priceObservable == null)
            throw new NullArgumentException();
        Response<Indicator> indicatorResponse = getIndicatorResponse(settings);
        priceObservable.registerObserver(transformToIndicatorObserver(indicatorResponse));
    }

    Request<?> getRequest(String controllerName, HashMap<String, String> settings){
        return requestBuilder.build(controllerName, settings);
    }

    UseCase make(String useCaseName){
        return useCaseFactory.make(useCaseName);
    }

    private IndicatorObserver transformToIndicatorObserver(Response<Indicator> indicatorResponse) {
        return IndicatorObserver.create(indicatorResponse.getResponseDataStructure());
    }

    private Response<Indicator> getIndicatorResponse(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = make(controllerName);
        Request<?> request = getRequest(controllerName, settings);
        return useCase.execute(request);
    }

}
