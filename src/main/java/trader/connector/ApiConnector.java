package trader.connector;

import trader.indicator.CandlesUpdaterConnector;
import trader.order.Order;
import trader.strategy.PriceConnector;
import trader.trade.entitie.Trade;

import java.util.List;

public interface ApiConnector extends CandlesUpdaterConnector, PriceConnector {

    static ApiConnector create(String apiName) {
        return BaseConnector.create(apiName);
    }

    List<Order> getOpenOrders();

    List<Trade> getOpenTrades();
}
