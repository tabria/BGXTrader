package trader.strategies.BGXStrategy;

import trader.candle.CandleGranularity;

public class StrategyConfig {

    private StrategyConfig(){}

    public static final CandleGranularity CANDLE_GRANULARITY = CandleGranularity.M30;
    public static final String INSTRUMENT_NAME = "EUR_USD";
    public static long INITIAL_CANDLES_QUANTITY = 5000L;
    public static long UPDATE_CANDLES_QUANTITY = 2L;
    public static int BIG_DECIMAL_SCALE = 5;

}
