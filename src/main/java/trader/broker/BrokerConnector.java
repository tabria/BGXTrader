package trader.broker;

import trader.price.Price;

public interface BrokerConnector {


    Price getPrice(String instrument);

//    List<Order> getOpenOrders();
//
//    List<Trade> getOpenTrades();
}
