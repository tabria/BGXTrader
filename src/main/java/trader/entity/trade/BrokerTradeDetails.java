package trader.entity.trade;

import java.math.BigDecimal;

public interface BrokerTradeDetails {
    String getTradeID();

    void setTradeID(String tradeID);

    String getStopLossOrderID();

    void setStopLossOrderID(String stopLossOrderID);

    BigDecimal getOpenPrice();

    void setOpenPrice(String openPrice);

    BigDecimal getStopLossPrice();

    void setStopLossPrice(String stopLossPrice);

    BigDecimal getCurrentUnits();

    void setCurrentUnits(String currentUnits);
}
