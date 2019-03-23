package trader.strategy.BGXStrategy;

import trader.connector.BaseConnector;
import trader.exception.NullArgumentException;
import trader.strategy.Strategy;
import trader.trade.service.OrderService;
import trader.trade.service.exit_strategie.BaseExitStrategy;
import trader.trade.service.exit_strategie.ExitStrategy;

public final class BGXStrategy implements Strategy {

    private ExitStrategy exitStrategy;
    private OrderService orderService;
    private BaseConnector baseConnector;

    public BGXStrategy(BaseConnector connector) {
        setBaseConnector(connector);
        orderService = new OrderService(baseConnector);
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

    public void setBaseConnector(BaseConnector baseConnector) {
        if(baseConnector == null){
            throw new NullArgumentException();
        }
        this.baseConnector = baseConnector;
    }

    private boolean haveOpenOrders() {
        return baseConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return baseConnector.getOpenTrades().size()>0;
    }

}
