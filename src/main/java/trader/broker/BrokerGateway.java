package trader.broker;

import trader.broker.connector.BrokerConnector;
import trader.entity.candlestick.Candlestick;
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
    String getNotFilledOrderID();
    String placeMarketIfTouchedOrder(HashMap<String, String> settings);

//    List<TradeImpl> getOpenTrades();

//    List<Order> getOpenOrders();

}
