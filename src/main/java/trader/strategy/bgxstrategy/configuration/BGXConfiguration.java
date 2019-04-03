package trader.strategy.bgxstrategy.configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


public interface BGXConfiguration {
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
}
