package trader.strategy.bgxstrategy;

import trader.connector.ApiConnector;
import trader.strategy.Observable;
import trader.exception.NullArgumentException;
import trader.entity.indicator.observer.IndicatorObserver;
import trader.order.OrderStrategy;
import trader.strategy.observable.PriceObservable;
import trader.strategy.Strategy;
import trader.order.OrderService;
import trader.exit.exit_strategie.BaseExitStrategy;
import trader.exit.ExitStrategy;
import java.util.List;

public final class BGXStrategy implements Strategy {

    private ApiConnector apiConnector;
    private List<IndicatorObserver> indicatorObservers;
    private Observable priceObservable;

    private ExitStrategy exitStrategy;
    private OrderStrategy orderStrategy;


    public BGXStrategy(ApiConnector connector) {
        setApiConnector(connector);
        priceObservable = PriceObservable.create(apiConnector);
        orderStrategy = new OrderService(apiConnector);
        exitStrategy = BaseExitStrategy.createInstance();
        init();
    }

    private void setIndicatorObservers(ConstructIndicatorsService constructIndicatorsService) {
        indicatorObservers = constructIndicatorsService.getIndicatorObservers();
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

    private void setApiConnector(ApiConnector connector) {
        if(connector == null){
            throw new NullArgumentException();
        }
        this.apiConnector = connector;
    }

    private void init(){
        ConstructIndicatorsService constructIndicatorsService = new ConstructIndicatorsService(apiConnector);
        setIndicatorObservers(constructIndicatorsService);
    }

    private boolean haveOpenOrders() {
        return apiConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return apiConnector.getOpenTrades().size()>0;
    }

}
