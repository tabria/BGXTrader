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

    public void execute(String indicatorType,
                        HashMap<String, String> settings, Observable priceObservable){
        if(priceObservable == null)
            throw new NullArgumentException();
        Response<Indicator> indicatorResponse = getIndicatorResponse(indicatorType, settings);
        priceObservable.registerObserver(transformToIndicatorObserver(indicatorResponse));
    }

    Request<?> getRequest(String useCaseName, HashMap<String, String> settings){
        return requestBuilder.build(useCaseName, settings);
    }

    UseCase make(String useCaseName){
        return useCaseFactory.make(useCaseName);
    }

    private IndicatorObserver transformToIndicatorObserver(Response<Indicator> indicatorResponse) {
        return IndicatorObserver.create(indicatorResponse.getResponseDataStructure());
    }

    private Response<Indicator> getIndicatorResponse(String indicatorType, HashMap<String, String> settings) {
        UseCase useCase = make(this.getClass().getSimpleName());
        Request<?> request = getRequest(indicatorType, settings);
        return useCase.execute(request);
    }

}
