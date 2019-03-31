package trader.strategy.bgxstrategy.configuration;

import trader.candlestick.candle.CandleGranularity;

import java.math.BigDecimal;

public class StrategyConfig {

    private StrategyConfig(){}

    public static final CandleGranularity CANDLE_GRANULARITY = CandleGranularity.M30;
    public static final String INSTRUMENT_NAME = "EUR_USD";
    public static long INITIAL_CANDLES_QUANTITY = 5000L;
    public static long UPDATE_CANDLES_QUANTITY = 2L;
    public static int SCALE = 5;
    public static final BigDecimal RISK_PER_TRADE = BigDecimal.valueOf(0.01).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    public static final BigDecimal SPREAD = BigDecimal.valueOf(0.0002).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    public static final double MIN_BALANCE = 1.0D;

    public static final String[] PRICE_SMA_SETTINGS = {"1", "CLOSE", "SIMPLE"};
    public static final String[] DAILY_SMA_SETTINGS = {"1", "OPEN", "SIMPLE"};
    public static final String[] FAST_WMA_SETTINGS = {"5", "CLOSE", "WEIGHTED"};
    public static final String[] MIDDLE_WMA_SETTINGS = {"20", "CLOSE", "WEIGHTED"};
    public static final String[] SLOW_WMA_SETTINGS = {"100", "CLOSE", "WEIGHTED"};
    public static final String[] RSI_SETTINGS = {"14", "CLOSE"};



}
