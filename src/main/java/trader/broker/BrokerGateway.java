package trader.broker;

import trader.entity.candlestick.Candlestick;
import trader.price.Price;
import trader.trade.entitie.Trade;

import java.util.HashMap;
import java.util.List;

public interface BrokerGateway {


    Price getPrice(String instrument);
    List<Candlestick> getCandles(HashMap<String, String> settings);
    void validateConnector();
    int totalTradesSize();
//    List<Trade> getOpenTrades();

//    List<Order> getOpenOrders();

}
