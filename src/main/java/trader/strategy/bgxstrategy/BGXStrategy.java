package trader.strategy.bgxstrategy;

import trader.connector.ApiConnector;
import trader.controller.AddIndicatorController;
import trader.interactor.RequestBuilderImpl;
import trader.interactor.UseCaseFactoryImpl;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.strategy.Observable;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.strategy.observable.PriceObservable;
import trader.strategy.Strategy;
import trader.order.OrderService;
import trader.exit.exit_strategie.BaseExitStrategy;
import trader.exit.ExitStrategy;

import java.util.HashMap;

public final class BGXStrategy implements Strategy {

    private ApiConnector apiConnector;
    private Observable priceObservable;
    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    private ExitStrategy exitStrategy;
    private OrderStrategy orderStrategy;


    public BGXStrategy(ApiConnector connector) {
        setApiConnector(connector);
        priceObservable = PriceObservable.create(apiConnector);
        requestBuilder = new RequestBuilderImpl();
        useCaseFactory = new UseCaseFactoryImpl();

        orderStrategy = new OrderService(apiConnector);
        exitStrategy = BaseExitStrategy.createInstance();
     //   init();
    }


    @Override
    public void execute() {
        if(haveOpenTrades())
            exitStrategy.execute();
    //    if (haveOpenOrders())
        //    orderStrategy.closeUnfilledOrder();
      //  orderStrategy.submitNewOrder();
    }

    @Override
    public String toString() {
        return "bgxstrategy";
    }

    void addIndicators(String indicatorType, HashMap<String, String> settings){
        AddIndicatorController addIndicatorController = new AddIndicatorController(requestBuilder, useCaseFactory);
        addIndicatorController.execute(indicatorType, settings, priceObservable);
    }


    private void setApiConnector(ApiConnector connector) {
        if(connector == null){
            throw new NullArgumentException();
        }
        this.apiConnector = connector;
    }


    private boolean haveOpenOrders() {
        return apiConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return apiConnector.getOpenTrades().size()>0;
    }

}
