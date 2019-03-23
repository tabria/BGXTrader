package trader.strategy.BGXStrategy;

import trader.connector.ApiConnector;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.strategy.Strategy;
import trader.order.OrderService;
import trader.exit.exit_strategie.BaseExitStrategy;
import trader.exit.ExitStrategy;

public final class BGXStrategy implements Strategy {

    private ExitStrategy exitStrategy;
    private OrderStrategy orderStrategy;
    private ApiConnector apiConnector;

    public BGXStrategy(ApiConnector connector) {
        setApiConnector(connector);
        orderStrategy = new OrderService(apiConnector);
        exitStrategy = BaseExitStrategy.createInstance();
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
        return "BGXStrategy";
    }

    public void setApiConnector(ApiConnector connector) {
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
