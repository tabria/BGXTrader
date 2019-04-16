package trader.configuration;

import trader.entity.candlestick.candle.CandleGranularity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface TradingStrategyConfiguration {
    List<Map<String, String>> getIndicators();

    void addIndicator(Map<String, String> indicator);

    String getInstrument();

    BigDecimal getSpread();

    long getInitialCandlesQuantity();

    void setInitialCandlesQuantity(String initialCandlesQuantity);

    long getUpdateCandlesQuantity();

    void setUpdateCandlesQuantity(String updateCandlesQuantity);

    BigDecimal getRiskPerTrade();

    void setRiskPerTrade(BigDecimal riskPerTrade);

    String getEntryStrategy();

    void setEntryStrategy(String entryStrategy);

    String getOrderStrategy();

    void setOrderStrategy(String orderStrategy);

    String getExitStrategy();

    void setExitStrategy(String exitStrategy);

    BigDecimal getStopLossFilter();

    void setStopLossFilter(BigDecimal stopLossFilter);

    BigDecimal getTarget();

    void setTarget(BigDecimal target);

    BigDecimal getRsiFilter();

    void setRsiFilter(BigDecimal rsiFilter);

    BigDecimal getEntryFilter();

    void setEntryFilter(BigDecimal entryFilter);

    CandleGranularity getExitGranularity();

    void setExitGranularity(CandleGranularity exitGranularity);
}
