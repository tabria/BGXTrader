package trader.broker;

import trader.broker.connector.BrokerConnector;
import trader.entity.candlestick.Candlestick;
import trader.entity.order.Order;
import trader.entity.order.enums.OrderType;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BrokerGateway {


    Price getPrice(String instrument);
    List<Candlestick> getCandles(HashMap<String, String> settings);

    void validateConnector();
    BrokerConnector getConnector();

    BigDecimal getMarginUsed();
    BigDecimal getAvailableMargin();
    BigDecimal getBalance();


    Order getOrder(OrderType orderType);
    int totalOpenOrdersSize();
    String cancelOrder(String orderID);
    String setTradeStopLossPrice(String tradeID, String price);
    BigDecimal getTradeStopLossPrice(String tradeID);
    String placeOrder(Map<String, String> settings, String orderType);


    BrokerTradeDetails getTradeDetails(int index);
    int totalOpenTradesSize();

}
