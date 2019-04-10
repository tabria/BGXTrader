package trader.order;

import trader.entity.trade.Trade;
import trader.price.Price;

import java.math.BigDecimal;

public interface OrderStrategy {

    BigDecimal getPipValue(Price price);

    BigDecimal calculateStopSize(Trade trade);
}
