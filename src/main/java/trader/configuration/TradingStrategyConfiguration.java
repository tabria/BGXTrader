package trader.configuration;

import trader.entity.candlestick.candle.CandleGranularity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


public interface TradingStrategyConfiguration {
    List<HashMap<String, String>> getIndicators();

    void addIndicator(HashMap<String, String> indicator);

    String getInstrument();

    BigDecimal getSpread();

    String getFileLocation();

    void setFileLocation(String fileLocation);

    long getInitialCandlesQuantity();

    void setInitialCandlesQuantity(long initialCandlesQuantity);

    long getUpdateCandlesQuantity();

    void setUpdateCandlesQuantity(long updateCandlesQuantity);

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
