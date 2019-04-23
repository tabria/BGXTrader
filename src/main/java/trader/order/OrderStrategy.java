package trader.order;

import trader.broker.BrokerGateway;
import trader.strategy.TradingStrategyConfiguration;
import trader.entity.trade.Trade;
import trader.entity.price.Price;

import java.math.BigDecimal;

public interface OrderStrategy {

    void placeTradeAsOrder(BrokerGateway brokerGateway, Price price, Trade trade, TradingStrategyConfiguration configuration);

    BigDecimal calculateUnitsSize(BrokerGateway brokerGateway, Price price, Trade trade, TradingStrategyConfiguration configuration);

    BigDecimal getPipValue(Price price);

    BigDecimal calculateStopSize(Trade trade);

    BigDecimal calculateTradeMargin(BrokerGateway brokerGateway, BigDecimal unitsSize);

    void closeUnfilledOrders(BrokerGateway brokerGateway, Price price);

}
