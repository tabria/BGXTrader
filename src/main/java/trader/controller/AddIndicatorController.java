package trader.controller;

import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.Observable;
import trader.strategy.observable.PriceObservable;

import java.util.HashMap;
public class AddIndicatorController{

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private UpdateIndicatorController updateIndicatorController;
    private Observable priceObservable;

    public AddIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, UpdateIndicatorController updateIndicatorController, Observable priceObservable) {
        if(requestBuilder == null || useCaseFactory == null
                || updateIndicatorController == null || priceObservable == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
        this.updateIndicatorController = updateIndicatorController;
        this.priceObservable = priceObservable;
    }

    public void execute(HashMap<String, String> settings){
        Response<Indicator> indicatorResponse = getIndicatorResponse(settings);
        priceObservable.registerObserver(transformToIndicatorObserver(indicatorResponse));
    }

    Request<?> getRequest(String controllerName, HashMap<String, String> settings){
        return requestBuilder.build(controllerName, settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }

    private IndicatorObserver transformToIndicatorObserver(Response<Indicator> indicatorResponse) {
        return IndicatorObserver.create(indicatorResponse.getResponseDataStructure(), updateIndicatorController);
    }

    private Response<Indicator> getIndicatorResponse(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = make(controllerName);
        Request<?> request = getRequest(controllerName, settings);
        return useCase.execute(request);
    }

}
