package trader.broker;

import trader.broker.connector.BaseConnector;
import trader.price.Pricing;

public interface BrokerConnector {


    Pricing getPrice(String instrument);

//    List<Order> getOpenOrders();
//
//    List<Trade> getOpenTrades();
}
