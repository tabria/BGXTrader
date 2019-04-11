package trader.broker;

import trader.broker.connector.BrokerConnector;
import trader.entity.candlestick.Candlestick;
import trader.entity.order.Order;
import trader.entity.order.enums.OrderType;
import trader.price.Price;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface BrokerGateway {


    Price getPrice(String instrument);
    List<Candlestick> getCandles(HashMap<String, String> settings);
    void validateConnector();
    int totalOpenTradesSize();
    int totalOpenOrdersSize();
    BigDecimal getMarginUsed();
    BigDecimal getAvailableMargin();
    BigDecimal getBalance();
    BrokerConnector getConnector();
    Order getOrder(OrderType orderType);
    String placeMarketIfTouchedOrder(HashMap<String, String> settings);


}
