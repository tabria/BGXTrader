package trader.controller;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.observer.UpdateIndicatorObserver;
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
    private Observable priceObservable;
    private TradingStrategyConfiguration configuration;
    private BrokerGateway gateway;

    public AddIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, Observable priceObservable, TradingStrategyConfiguration configuration, BrokerGateway gateway) {
        if(requestBuilder == null || useCaseFactory == null  || priceObservable == null
                || configuration == null || gateway == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
        this.priceObservable = priceObservable;
        this.configuration = configuration;
        this.gateway = gateway;
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

    private UpdateIndicatorObserver transformToIndicatorObserver(Response<T> indicatorResponse) {
        return new UpdateIndicatorObserver((Indicator)indicatorResponse.getResponseDataStructure(), configuration, gateway);
    }

    private Response<T> getIndicatorResponse(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = make(controllerName);
        Request<?> request = getRequest(controllerName, settings);
        return useCase.execute(request);
    }

}
