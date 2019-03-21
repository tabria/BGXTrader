package trader.strategy.BGXStrategy;

import trader.candle.CandleGranularity;

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


}
