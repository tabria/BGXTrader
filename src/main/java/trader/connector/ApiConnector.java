package trader.connector;

import trader.candlestick.Candlestick;
import trader.order.Order;
import trader.price.Pricing;
import trader.trade.entitie.Trade;

import java.util.List;

public interface ApiConnector extends CandlesUpdaterConnector, PriceConnector {

    static ApiConnector create(String apiName) {
        return BaseConnector.create(apiName);
    }

    List<Order> getOpenOrders();

    List<Trade> getOpenTrades();
}
