package trader.broker.connector;

import trader.entity.candlestick.Candlestick;
import trader.entity.order.Order;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;
import trader.responder.Response;

import java.util.List;

public interface Transformable {

    <T> BrokerTradeDetails transformTradeSummary(T tradeSummary, List<com.oanda.v20.order.Order> orders);

    <T> Order transformOrder(T order);

    <T> List<Candlestick> transformCandlesticks(Response<T> response);

    <T> Price transformToPrice(Response<T> response);

}
