package trader.entity.order;

import trader.entity.order.enums.OrderType;

import java.math.BigDecimal;

public interface Order {
    String getId();

    OrderType getOrderType();

    String getInstrument();

    BigDecimal getUnits();

    BigDecimal getStopLossPrice();
}
