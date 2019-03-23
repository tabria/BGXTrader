package trader.strategy.BGXStrategy;

import trader.connector.ApiConnector;
import trader.exception.NullArgumentException;
import trader.strategy.Strategy;
import trader.trade.service.OrderService;
import trader.trade.service.exit_strategie.BaseExitStrategy;
import trader.trade.service.exit_strategie.ExitStrategy;

public final class BGXStrategy implements Strategy {

    private ExitStrategy exitStrategy;
    private OrderService orderService;
    private ApiConnector apiConnector;

    public BGXStrategy(ApiConnector connector) {
        setApiConnector(connector);
        orderService = new OrderService(apiConnector);
        exitStrategy = BaseExitStrategy.createInstance();
    }

    @Override
    public void execute() {
        if(haveOpenTrades())
            exitStrategy.execute();
        if (haveOpenOrders())
            orderService.closeUnfilledOrder();
        orderService.submitNewOrder();
    }

    @Override
    public String toString() {
        return "BGXStrategy";
    }

    public void setApiConnector(ApiConnector apiConnector) {
        if(apiConnector == null){
            throw new NullArgumentException();
        }
        this.apiConnector = apiConnector;
    }

    private boolean haveOpenOrders() {
        return apiConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return apiConnector.getOpenTrades().size()>0;
    }

}
