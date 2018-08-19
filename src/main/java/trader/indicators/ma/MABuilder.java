package trader.indicators.ma;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import trader.candles.CandlesUpdater;
import trader.config.Config;
import trader.indicators.Indicator;
import trader.indicators.enums.AppliedPrice;
import trader.indicators.ma.enums.MAType;


/**
 *  Builder for creating moving average
 */

public final class MABuilder {

    private static final long DEFAULT_PERIOD = 20L;
    private static final long MIN_PERIOD = 1L;
    private static final long MAX_PERIOD = 4000L;
    private static final CandlestickGranularity DEFAULT_CANDLE_TIME_FRAME = CandlestickGranularity.H4;
    private static final AppliedPrice DEFAULT_APPLIED_PRICE = AppliedPrice.CLOSE;
    private static final MAType DEFAULT_MA_TYPE = MAType.SIMPLE;

    private Context ctx;
    private long period;
    private CandlestickGranularity candleTimeFrame;
    private AppliedPrice appliedPrice;
    private MAType maType;

    /**
     * Builder Constructor.
     *
     * @param context api context
     * @see Context
     */
    public MABuilder(Context context) {
        setContext(context);
        this.period = DEFAULT_PERIOD;
        this.candleTimeFrame = DEFAULT_CANDLE_TIME_FRAME;
        this.appliedPrice = DEFAULT_APPLIED_PRICE;
        this.maType = DEFAULT_MA_TYPE;
    }

    /**
     * Set candles time frame, from which moving average will be calculated.
     *
     * @param candleTimeFrame is the time frame of the indicator
     * @return {@link MABuilder}current builder object
     * @throws NullPointerException when time frame is null
     * @see com.oanda.v20.instrument.CandlestickGranularity
     */
    public MABuilder setCandleTimeFrame(CandlestickGranularity candleTimeFrame){
        if (candleTimeFrame == null){
            throw new NullPointerException("Candle time frame is null");
        }
        this.candleTimeFrame = candleTimeFrame;
        return this;
    }

    /**
     * Set the number of candles to be used for calculating the moving average
     *
     * @param period number of candles
     * @return {@link MABuilder} current builder object
     * @throws IllegalArgumentException when period is less than MIN_PERIOD or bigger than MAX_PERIOD
     */
    public MABuilder setPeriod(long period){
        if (period < MIN_PERIOD || period > MAX_PERIOD){
            throw new IllegalArgumentException (
                    String.format("Period value is %d, must be between %d and %d.", period, MIN_PERIOD, MAX_PERIOD)
            );
        }
        this.period = period;
        return this;
    }

    /**
     * Set the appliedPrice.
     * This is the price which will be use to calculate SMA. The price is taken or calculated from candle structure - High, Low, Open, Close
     *
     * @param appliedPrice candle price
     * @return {@link MABuilder} current builder object
     * @throws NullPointerException when appliedPrice is null
     * @see AppliedPrice
     */
    public MABuilder setAppliedPrice(AppliedPrice appliedPrice){
        if(appliedPrice == null){
            throw new NullPointerException("AppliedPrice must not be null");
        }
        this.appliedPrice = appliedPrice;
        return this;
    }

    /**
     * Set the type of the moving average. Based on the type the value of ma will be different
     * @param maType moving average type
     * @return {@link MABuilder} current builder object
     * @throws NullPointerException when MAType is null
     * @see MAType
     */
    public MABuilder setMAType(MAType maType){
        if(maType == null){
            throw new NullPointerException("Type of the moving average must not be null");
        }
        this.maType = maType;
        return this;
    }

    /**
     * Build Moving Average
     *
     * @return {@link Indicator} new Indicator object
     * @see Indicator
     */
    public Indicator build(){
        InstrumentCandlesRequest request = createRequest();
        CandlesUpdater updater = new CandlesUpdater(this.ctx, request, this.candleTimeFrame);

        if (this.maType.equals(MAType.EXPONENTIAL)){
            return new ExponentialMA(period, appliedPrice, updater);

        } else if (this.maType.equals(MAType.WEIGHTED)){
            return new WeightedMA(period, appliedPrice, updater);

        } else {
            return new SimpleMA(period, appliedPrice, updater);
        }
    }

    /**
     * Create Candle Request Object
     * @return {@link InstrumentCandlesRequest} new request object for the OANDA api
     * @see InstrumentCandlesRequest
     */
    private InstrumentCandlesRequest createRequest(){


        long candlesCount = this.period * 2L + 2L;

        return new InstrumentCandlesRequest(Config.INSTRUMENT)
                    .setCount(candlesCount)
                    .setGranularity(this.candleTimeFrame)
                    .setSmooth(false);
    }

    /**
     * Set context
     * @param context the api context
     * @throws NullPointerException when context is null
     * @see Context
     */
    private void setContext(Context context){
        if (context == null){
            throw new NullPointerException("Context is null");
        }
        this.ctx = context;
    }

}
