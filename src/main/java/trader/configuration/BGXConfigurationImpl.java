package trader.configuration;

import trader.exception.BadRequestException;
import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BGXConfigurationImpl implements TradingStrategyConfiguration {

    private static final String DEFAULT_INSTRUMENT = "EUR_USD";
    private static final long DEFAULT_INITIAL_CANDLES_QUANTITY = 4999L;
    private static final long DEFAULT_UPDATE_CANDLES_QUANTITY = 2L;
    private static final String DEFAULT_BGX_CONFIG_FILE_LOCATION = "bgxStrategyConfig.yaml";
    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_RISK_PER_TRADE = BigDecimal.valueOf(0.01).setScale(5, BigDecimal.ROUND_HALF_UP);

    private String fileLocation;
    private List<HashMap<String, String>> indicators;
    private long initialCandlesQuantity;
    private long updateCandlesQuantity;
    private String instrument;
    private BigDecimal spread;
    private BigDecimal riskPerTrade;


    public BGXConfigurationImpl() {
        this.indicators = new ArrayList<>();
        this.initialCandlesQuantity = DEFAULT_INITIAL_CANDLES_QUANTITY;
        this.updateCandlesQuantity = DEFAULT_UPDATE_CANDLES_QUANTITY;
        this.instrument = DEFAULT_INSTRUMENT;
        this.spread = DEFAULT_SPREAD;
        this.fileLocation = DEFAULT_BGX_CONFIG_FILE_LOCATION;
        this.riskPerTrade = DEFAULT_RISK_PER_TRADE;
    }

    @Override
    public List<HashMap<String, String>> getIndicators() {
        return this.indicators;
    }

    @Override
    public void addIndicator(HashMap<String, String> indicator) {
        if(indicator == null)
            throw new NullArgumentException();
        this.indicators.add(indicator);
    }

    @Override
    public String getInstrument() {
        return this.instrument;
    }

    @Override
    public BigDecimal getSpread(){
        return this.spread;
    }

    @Override
    public String getFileLocation() {
        return fileLocation;
    }

    @Override
    public void setFileLocation(String fileLocation) {
        validateInputFileLocation(fileLocation);
        if(fileLocation.isEmpty())
            return;
        this.fileLocation = fileLocation.trim();
    }

    @Override
    public long getInitialCandlesQuantity() {
        return initialCandlesQuantity;
    }

    @Override
    public void setInitialCandlesQuantity(long initialCandlesQuantity) {
        if(initialCandlesQuantity > 0)
            this.initialCandlesQuantity = initialCandlesQuantity;
    }

    @Override
    public long getUpdateCandlesQuantity() {
        return updateCandlesQuantity;
    }

    @Override
    public void setUpdateCandlesQuantity(long updateCandlesQuantity) {
        if(updateCandlesQuantity > 0)
            this.updateCandlesQuantity = updateCandlesQuantity;
    }

    @Override
    public BigDecimal getRiskPerTrade() {
        return riskPerTrade;
    }

    @Override
    public void setRiskPerTrade(BigDecimal riskPerTrade) {
        if(riskPerTrade == null)
            throw new NullArgumentException();
        if(riskPerTrade.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeNumberException();
        this.riskPerTrade = riskPerTrade;
    }

    private void validateInputFileLocation(String fileLocation) {
        if(fileLocation == null)
            throw new NullArgumentException();
        if(isNotYamlFile(fileLocation))
            throw new BadRequestException();
    }

    private boolean isNotYamlFile(String fileLocation) {
        if(fileLocation.isEmpty())
            return false;
        if(fileLocation.contains(".yaml"))
            return false;
        return !fileLocation.contains(".yml");
    }
}
