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

    private Context context;
    private long candlesticksQuantity;
    private CandlestickPriceType candlestickPriceType;
    private CandleGranularity candleGranularity;

    /**
     * Constructor for RSIBuileder
     * @param context oanda api contex
     * @see Context
     */
    public RSIBuilder(Context context){
            setContext(context);
            setCandlesticksQuantity(DEFAULT_CANDLESTICKS_QUANTITY);
            setCandlestickPriceType(DEFAULT_CANDLESTICK_PRICE_TYPE);
            setCandleGranularity(DEFAULT_CANDLE_GRANULARITY);
    }

    /**
     * Set candlesticksQuantity
     * @param candlesticksQuantity candlesticksQuantity for the RSI
     * @throws IllegalArgumentException when candlesticksQuantity is: {@code candlesticksQuantity < MIN_PERIOD || candlesticksQuantity > MAX_CANDLESTICKS_QUANTITY}
     * @return {@link RSIBuilder} current builder object
     */
    public RSIBuilder setCandlesticksQuantity(long candlesticksQuantity) {
        if (candlesticksQuantity < MIN_CANDLESTICKS_QUANTITY || candlesticksQuantity > MAX_CANDLESTICKS_QUANTITY){
            throw  new IllegalArgumentException("Period must be between " + MIN_CANDLESTICKS_QUANTITY + " and " + MAX_CANDLESTICKS_QUANTITY);
        }
        this.candlesticksQuantity = candlesticksQuantity;
        return this;
    }

    /**
     * Set candlestickPriceType
     * @param candlestickPriceType the price part of the candle on which indicator will be calculated
     * @return {@link RSIBuilder} current builder object
     * @throws IllegalArgumentException when candlestickPriceType is null
     * @see CandlestickPriceType
     */
    public RSIBuilder setCandlestickPriceType(CandlestickPriceType candlestickPriceType) {
        if (candlestickPriceType == null){
            throw  new NullPointerException("candlestickPriceType must not be null");
        }
        this.candlestickPriceType = candlestickPriceType;
        return this;
    }

    /**
     * Set candles time frame
     * @param candleGranularity candles time frame
     * @return {@link RSIBuilder} current builder object
     * @throws NullPointerException when candleGranularity is null
     */
    public RSIBuilder setCandleGranularity(CandleGranularity candleGranularity) {
        if (candleGranularity == null){
            throw new NullPointerException("Candles time frame must not be null");
        }
        this.candleGranularity = candleGranularity;
        return this;
    }


    public Indicator build(){
        InstrumentCandlesRequest request = createCandlesRequest(this.candlesticksQuantity, this.candleGranularity);
        CandlesUpdater updater = new CandlesUpdater(this.context, request, this.candleGranularity);
        return new RelativeStrengthIndex(this.candlesticksQuantity, this.candlestickPriceType, updater);
    }

    /**
     * Set context
     * @param context oanda context
     * @throws NullPointerException if context is null
     * @see Context
     */
    private void setContext(Context context) {
        if (context == null){
            throw new NullPointerException("Context must not be null");
        }
        this.context = context;
    }

    /**
     * Set Candle Request Object.
     * Candles count affect result greatly. On small time frame (M1, M5)  {@code candlesCount} must be as much as possible.
     * @param period current candlesticksQuantity
     * @param candlesTimeFrame current time frame
     * @see InstrumentCandlesRequest
     */
    private InstrumentCandlesRequest createCandlesRequest(long period, CandleGranularity candlesTimeFrame){
        long candlesCount = period * 20;
            return new InstrumentCandlesRequest(Config.INSTRUMENT)
                    .setCount(candlesCount)
                    .setGranularity(candlesTimeFrame.extractOANDAGranularity())
                    .setSmooth(false);
    }

}
