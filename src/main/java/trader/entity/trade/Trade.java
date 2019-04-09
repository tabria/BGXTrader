package trader.entity.trade;

import java.math.BigDecimal;

public interface Trade {
    Direction getDirection();

    void setDirection(String direction);

    boolean getTradable();

    void setTradable(String tradable);

    BigDecimal getEntryPrice();

    void setEntryPrice(String entryPrice);

    BigDecimal getStopLossPrice();

    void setStopLossPrice(String stopLossPrice);
}
