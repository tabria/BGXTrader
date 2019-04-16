package trader.configuration;

import trader.entity.candlestick.candle.CandleGranularity;
import trader.exception.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BGXConfigurationImpl implements TradingStrategyConfiguration {

    private static final String DEFAULT_INSTRUMENT = "EUR_USD";
    private static final long MAX_CANDLES_QUANTITY = 4999L;
    private static final long DEFAULT_UPDATE_CANDLES_QUANTITY = 2L;
    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_RISK_PER_TRADE = BigDecimal.valueOf(0.01)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final String DEFAULT_ENTRY_STRATEGY = "standard";
    private static final BigDecimal DEFAULT_ENTRY_FILTER = BigDecimal.valueOf(0.0020)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal RSI_FILTER = BigDecimal.valueOf(50)
            .setScale(5, RoundingMode.HALF_UP);
    private static final String DEFAULT_ORDER_STRATEGY = "standard";
    private static final String DEFAULT_EXIT_STRATEGY = "fullClose";
    private static final CandleGranularity DEFAULT_EXIT_GRANULARITY = CandleGranularity.M30;

    private List<Map<String, String>> indicators;
    private long initialCandlesQuantity;
    private long updateCandlesQuantity;
    private String instrument;
    private BigDecimal spread;
    private BigDecimal riskPerTrade;
    private String entryStrategy;
    private BigDecimal stopLossFilter;
    private BigDecimal target;
    private BigDecimal rsiFilter;
    private BigDecimal entryFilter;
    private String orderStrategy;
    private String exitStrategy;
    private CandleGranularity exitGranularity;



    public BGXConfigurationImpl() {
        this.indicators = new ArrayList<>();
        this.initialCandlesQuantity = MAX_CANDLES_QUANTITY;
        this.updateCandlesQuantity = DEFAULT_UPDATE_CANDLES_QUANTITY;
        this.instrument = DEFAULT_INSTRUMENT;
        this.spread = DEFAULT_SPREAD;
        this.riskPerTrade = DEFAULT_RISK_PER_TRADE;
        this.entryStrategy = DEFAULT_ENTRY_STRATEGY;
        this.stopLossFilter = DEFAULT_STOP_LOSS_FILTER;
        this.target = FIRST_TARGET;
        this.rsiFilter = RSI_FILTER;
        this.entryFilter = DEFAULT_ENTRY_FILTER;
        this.orderStrategy = DEFAULT_ORDER_STRATEGY;
        this.exitStrategy = DEFAULT_EXIT_STRATEGY;
        this.exitGranularity = DEFAULT_EXIT_GRANULARITY;
    }

    @Override
    public List<Map<String, String>> getIndicators() {
        return this.indicators;
    }

    @Override
    public void addIndicator(Map<String, String> indicator) {
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
    public long getInitialCandlesQuantity() {
        return initialCandlesQuantity;
    }

    @Override
    public void setInitialCandlesQuantity(String initialCandlesQuantity) {
        if(initialCandlesQuantity != null && !initialCandlesQuantity.trim().isEmpty()){
            long quantity = parseToLong(initialCandlesQuantity);
            validateBoundaries(quantity, 0L, MAX_CANDLES_QUANTITY);
            this.initialCandlesQuantity = quantity;
        }
    }

    private void validateBoundaries(long number, long minValue, long maxValue) {
        if(number <= minValue)
            throw new UnderflowException();
        if(number > maxValue)
            throw new OverflowException();
    }

    private long parseToLong(String initialCandlesQuantity) {
        try{
           return Long.parseLong(initialCandlesQuantity.trim());
        } catch (Exception e){
            throw new NotANumberException();
        }
    }


    @Override
    public long getUpdateCandlesQuantity() {
        return updateCandlesQuantity;
    }

    @Override
    public void setUpdateCandlesQuantity(String updateCandlesQuantity) {
//        if(updateCandlesQuantity > 0)
//            this.updateCandlesQuantity = updateCandlesQuantity;
    }

    @Override
    public BigDecimal getRiskPerTrade() {
        return riskPerTrade;
    }

    @Override
    public void setRiskPerTrade(BigDecimal riskPerTrade) {
        validateBigDecimalInput(riskPerTrade);
        this.riskPerTrade = riskPerTrade;
    }

    @Override
    public String getEntryStrategy() {
        return entryStrategy;
    }

    @Override
    public void setEntryStrategy(String entryStrategy) {
        if(entryStrategy != null && !entryStrategy.trim().isEmpty())
            this.entryStrategy = entryStrategy.trim();
    }

    @Override
    public String getOrderStrategy() {
        return orderStrategy;
    }

    @Override
    public void setOrderStrategy(String orderStrategy) {
        if(orderStrategy != null && !orderStrategy.trim().isEmpty())
            this.orderStrategy = orderStrategy.trim();
    }

    @Override
    public String getExitStrategy() {
        return exitStrategy;
    }

    @Override
    public void setExitStrategy(String exitStrategy) {
        if(exitStrategy != null && !exitStrategy.trim().isEmpty())
            this.exitStrategy = exitStrategy.trim();
    }

    @Override
    public BigDecimal getStopLossFilter() {
        return stopLossFilter;
    }

    @Override
    public void setStopLossFilter(BigDecimal stopLossFilter) {
        validateBigDecimalInput(stopLossFilter);
        this.stopLossFilter = stopLossFilter;
    }

    @Override
    public BigDecimal getTarget() {
        return target;
    }

    @Override
    public void setTarget(BigDecimal target) {
        validateBigDecimalInput(target);
        this.target = target;
    }

    @Override
    public BigDecimal getRsiFilter() {
        return rsiFilter;
    }

    @Override
    public void setRsiFilter(BigDecimal rsiFilter) {
        validateBigDecimalInput(rsiFilter);
        this.rsiFilter = rsiFilter;
    }

    @Override
    public BigDecimal getEntryFilter() {
        return entryFilter;
    }

    @Override
    public void setEntryFilter(BigDecimal entryFilter) {
        validateBigDecimalInput(entryFilter);
        this.entryFilter = entryFilter;
    }

    @Override
    public CandleGranularity getExitGranularity() {
        return exitGranularity;
    }

    @Override
    public void setExitGranularity(CandleGranularity exitGranularity) {
        if(exitGranularity != null)
            this.exitGranularity = exitGranularity;
    }

    private void validateBigDecimalInput(BigDecimal number) {
        if(number == null)
            throw new NullArgumentException();
        if(number.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeNumberException();
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
