package trader.indicators.rsi;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import trader.candles.CandlesUpdater;
import trader.config.Config;
import trader.indicators.Indicator;
import trader.indicators.enums.CandleGranularity;
import trader.indicators.enums.CandlestickPriceType;

public final class RSIBuilder {

    private static final long DEFAULT_CANDLESTICKS_QUANTITY = 14L;
    private static final long MIN_CANDLESTICKS_QUANTITY = 1L;
    private static final long MAX_CANDLESTICKS_QUANTITY = 4000L;
    private static final CandlestickPriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlestickPriceType.CLOSE;
    private static final CandleGranularity DEFAULT_CANDLE_GRANULARITY = CandleGranularity.H4;
    private static final int REQUEST_CANDLES_MULTIPLIER = 20;

    private Context context;
    private long candlesticksQuantity;
    private CandlestickPriceType candlestickPriceType;
    private CandleGranularity candleGranularity;

    public RSIBuilder(Context context){
            setContext(context);
            setCandlesticksQuantity(DEFAULT_CANDLESTICKS_QUANTITY);
            setCandlestickPriceType(DEFAULT_CANDLESTICK_PRICE_TYPE);
            setCandleGranularity(DEFAULT_CANDLE_GRANULARITY);
    }

    public RSIBuilder setCandlesticksQuantity(long candlesticksQuantity) {
        if (notInBoundary(candlesticksQuantity))
            throw  new IllegalArgumentException(String.format("Candlesticks Quantity must be between %d and %d",
                    MIN_CANDLESTICKS_QUANTITY, MAX_CANDLESTICKS_QUANTITY));
        this.candlesticksQuantity = candlesticksQuantity;
        return this;
    }

    public RSIBuilder setCandlestickPriceType(CandlestickPriceType candlestickPriceType) {
        if (isNull(candlestickPriceType))
            throw  new NullPointerException("CandlestickPriceType must not be null");
        this.candlestickPriceType = candlestickPriceType;
        return this;
    }

    public RSIBuilder setCandleGranularity(CandleGranularity candleGranularity) {
        if (isNull(candleGranularity))
            throw new NullPointerException("Candles time frame must not be null");
        this.candleGranularity = candleGranularity;
        return this;
    }
    
    public Indicator build(){
        InstrumentCandlesRequest request = createCandlesRequest();
        CandlesUpdater updater = new CandlesUpdater(context, request, candleGranularity);
        return new RelativeStrengthIndex(candlesticksQuantity, candlestickPriceType, updater);
    }

    private void setContext(Context context) {
        if (isNull(context))
            throw new NullPointerException("Context must not be null");
        this.context = context;
    }
    
    private boolean notInBoundary(long candlesticksQuantity) {
        return candlesticksQuantity < MIN_CANDLESTICKS_QUANTITY || candlesticksQuantity > MAX_CANDLESTICKS_QUANTITY;
    }
    
    private boolean isNull(Object object) {
        return object == null;
    }

    private InstrumentCandlesRequest createCandlesRequest(){
        return new InstrumentCandlesRequest(Config.INSTRUMENT)
                    .setCount(candlesticksQuantity * REQUEST_CANDLES_MULTIPLIER)
                    .setGranularity(candleGranularity.extractOANDAGranularity())
                    .setSmooth(false);
    }
}
