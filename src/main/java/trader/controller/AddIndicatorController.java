package trader.controller;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.requestor.UseCase;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.Observable;

import java.util.HashMap;
public class AddIndicatorController<T> implements TraderController<T> {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private UpdateIndicatorController updateIndicatorController;
    private Observable priceObservable;
    private TradingStrategyConfiguration configuration;

    public AddIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, UpdateIndicatorController updateIndicatorController, Observable priceObservable, TradingStrategyConfiguration configuration) {
        if(requestBuilder == null || useCaseFactory == null  || updateIndicatorController == null
                || priceObservable == null || configuration == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
        this.updateIndicatorController = updateIndicatorController;
        this.priceObservable = priceObservable;
        this.configuration = configuration;
    }

    public Response<T> execute(HashMap<String, String> settings){
        Response<T> indicatorResponse = getIndicatorResponse(settings);
        priceObservable.registerObserver(transformToIndicatorObserver(indicatorResponse));
        return indicatorResponse;
    }

    Request<?> getRequest(String controllerName, HashMap<String, String> settings){
        return requestBuilder.build(controllerName, settings);
    }

    UseCase make(String controllerName){
        return useCaseFactory.make(controllerName);
    }

    private IndicatorObserver transformToIndicatorObserver(Response<T> indicatorResponse) {
        return IndicatorObserver.create((Indicator)indicatorResponse.getResponseDataStructure(), updateIndicatorController, configuration);
    }

    private Response<T> getIndicatorResponse(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = make(controllerName);
        Request<?> request = getRequest(controllerName, settings);
        return useCase.execute(request);
    }

}
