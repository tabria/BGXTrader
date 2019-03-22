package trader.strategy.BGXStrategy;

import trader.connector.ApiConnector;
import trader.strategy.Strategy;
import trader.trade.service.OrderService;
import trader.trade.service.exit_strategie.BaseExitStrategy;
import trader.trade.service.exit_strategie.ExitStrategy;

public final class BGXStrategy implements Strategy {

    private ExitStrategy exitStrategy;
    private OrderService orderService;
    private ApiConnector apiConnector;

    public BGXStrategy(ApiConnector connector) {
        apiConnector = connector;
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

    private boolean haveOpenOrders() {
        return apiConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return apiConnector.getOpenTrades().size()>0;
    }

}