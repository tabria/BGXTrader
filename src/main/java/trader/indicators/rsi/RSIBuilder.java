package trader.indicators.rsi;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;

import trader.candles.CandlesUpdater;
import trader.config.Config;
import trader.indicators.Indicator;
import trader.indicators.enums.CandlestickPrice;

public final class RSIBuilder {

    private static final long DEFAULT_PERIOD = 14L;
    private static final long MIN_PERIOD = 1L;
    private static final long MAX_PERIOD = 4000L;
    private static final CandlestickPrice DEFAULT_APPLIED_PRICE = CandlestickPrice.CLOSE;
    private static final CandlestickGranularity DEFAULT_CANDLE_TIME_FRAME = CandlestickGranularity.H4;

    private Context context;
    private long period;
    private CandlestickPrice candlestickPrice;
    private CandlestickGranularity candlesTimeFrame;

    /**
     * Constructor for RSIBuileder
     * @param context oanda api contex
     * @see Context
     */
    public RSIBuilder(Context context){
            setContext(context);
            setPeriod(DEFAULT_PERIOD);
            setCandlestickPrice(DEFAULT_APPLIED_PRICE);
            setCandlesTimeFrame(DEFAULT_CANDLE_TIME_FRAME);
    }

    /**
     * Set period
     * @param period period for the RSI
     * @throws IllegalArgumentException when period is: {@code period < MIN_PERIOD || period > MAX_PERIOD}
     * @return {@link RSIBuilder} current builder object
     */
    public RSIBuilder setPeriod(long period) {
        if (period < MIN_PERIOD || period > MAX_PERIOD){
            throw  new IllegalArgumentException("Period must be between " + MIN_PERIOD + " and " + MAX_PERIOD);
        }
        this.period = period;
        return this;
    }

    /**
     * Set candlestickPrice
     * @param candlestickPrice the price part of the candle on which indicator will be calculated
     * @return {@link RSIBuilder} current builder object
     * @throws IllegalArgumentException when candlestickPrice is null
     * @see CandlestickPrice
     */
    public RSIBuilder setCandlestickPrice(CandlestickPrice candlestickPrice) {
        if (candlestickPrice == null){
            throw  new NullPointerException("candlestickPrice must not be null");
        }
        this.candlestickPrice = candlestickPrice;
        return this;
    }

    /**
     * Set candles time frame
     * @param candlesTimeFrame candles time frame
     * @return {@link RSIBuilder} current builder object
     * @throws NullPointerException when candlesTimeFrame is null
     * @see CandlestickGranularity
     */
    public RSIBuilder setCandlesTimeFrame(CandlestickGranularity candlesTimeFrame) {
        if (candlesTimeFrame == null){
            throw new NullPointerException("Candles time frame must not be null");
        }
        this.candlesTimeFrame = candlesTimeFrame;
        return this;
    }


    public Indicator build(){
        InstrumentCandlesRequest request = createCandlesRequest(this.period, this.candlesTimeFrame);
        CandlesUpdater updater = new CandlesUpdater(this.context, request, this.candlesTimeFrame);
        return new RelativeStrengthIndex(this.period, this.candlestickPrice, updater);
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
     * @param period current period
     * @param candlesTimeFrame current time frame
     * @see InstrumentCandlesRequest
     * @see CandlestickGranularity
     */
    private InstrumentCandlesRequest createCandlesRequest(long period, CandlestickGranularity candlesTimeFrame){
        long candlesCount = period * 20;
            return new InstrumentCandlesRequest(Config.INSTRUMENT)
                    .setCount(candlesCount)
                    .setGranularity(candlesTimeFrame)
                    .setSmooth(false);
    }

}
