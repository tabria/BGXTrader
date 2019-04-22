package trader.observer;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.order.OrderStrategy;
import trader.entity.price.Price;

import java.math.BigDecimal;

public class PositionObserver extends BaseObserver {

    private static final BigDecimal TRADABLE_THRESHOLD = BigDecimal.valueOf(0.0020);
    private TradingStrategyConfiguration configuration;
    private EntryStrategy entryStrategy;
    private OrderStrategy orderStrategy;
    private ExitStrategy exitStrategy;

    public PositionObserver(BrokerGateway brokerGateway,
                            EntryStrategy entryStrategy,
                            OrderStrategy orderStrategy,
                            TradingStrategyConfiguration configuration,
                            ExitStrategy exitStrategy){
        super(brokerGateway);
        if(entryStrategy == null || orderStrategy == null || configuration == null || exitStrategy == null)
            throw new NullArgumentException();
        this.entryStrategy = entryStrategy;
        this.orderStrategy = orderStrategy;
        this.configuration = configuration;
        this.exitStrategy = exitStrategy;
    }

    @Override
    public void updateObserver(Price price) {
        if(brokerGateway.totalOpenTradesSize() == 0 && brokerGateway.totalOpenOrdersSize() == 0){
            Trade newTrade = entryStrategy.generateTrade();
            setTradableForThreshold(price, newTrade);
            if(isTradable(newTrade))
               orderStrategy.placeTradeAsOrder(brokerGateway, price, newTrade, configuration);
        } else if(brokerGateway.totalOpenOrdersSize() > 0) {
            orderStrategy.closeUnfilledOrders(brokerGateway, price);
        } else if(brokerGateway.totalOpenTradesSize() > 0){
            exitStrategy.execute(price);
        }
    }

    private boolean isTradable(Trade newTrade) {
        return newTrade.getTradable() && !newTrade.getDirection().equals(Direction.FLAT);
    }

    private void setTradableForThreshold(Price price, Trade newTrade) {
        if(isTradable(newTrade)) {
            BigDecimal delta = getEntryPriceAndPriceDelta(newTrade, price);
            if (delta.compareTo(TRADABLE_THRESHOLD) > 0)
                newTrade.setTradable("false");
        }
    }

    private BigDecimal getEntryPriceAndPriceDelta(Trade newTrade, Price price) {
        BigDecimal currentPrice = newTrade.getDirection().equals(Direction.UP) ? price.getAsk() : price.getBid();
        BigDecimal entryPrice = newTrade.getEntryPrice();
        return entryPrice.subtract(currentPrice).abs();
    }
}
