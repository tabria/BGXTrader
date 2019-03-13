package trader.indicators.ma;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import trader.candles.CandlesUpdater;
import trader.config.Config;
import trader.indicators.Indicator;
import trader.indicators.enums.CandlestickPriceType;
import trader.indicators.ma.enums.MAType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 *  Builder for creating moving average
 */

public final class MABuilder {

    private static final long DEFAULT_PERIOD = 20L;
    private static final long MIN_PERIOD = 1L;
    private static final long MAX_PERIOD = 4000L;
    private static final CandlestickGranularity DEFAULT_CANDLE_TIME_FRAME = CandlestickGranularity.H4;
    private static final CandlestickPriceType DEFAULT_APPLIED_PRICE = CandlestickPriceType.CLOSE;
    private static final MAType DEFAULT_MA_TYPE = MAType.SIMPLE;
    private static final long PERIOD_MULTIPLIER = 4L;
    private static final String MA_LOCATION = "trader.indicators.ma.";

    private Context ctx;
    private long period;
    private CandlestickGranularity candleTimeFrame;
    private CandlestickPriceType candlestickPriceType;
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
        this.candlestickPriceType = DEFAULT_APPLIED_PRICE;
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
    public MABuilder setCandlesQuantity(long period){
        if (period < MIN_PERIOD || period > MAX_PERIOD){
            throw new IllegalArgumentException (
                    String.format("Period value is %d, must be between %d and %d.", period, MIN_PERIOD, MAX_PERIOD)
            );
        }
        this.period = period;
        return this;
    }

    /**
     * Set the candlestickPriceType.
     * This is the price which will be use to calculate SMA. The price is taken or calculated from candle structure - High, Low, Open, Close
     *
     * @param candlestickPriceType candle price
     * @return {@link MABuilder} current builder object
     * @throws NullPointerException when candlestickPriceType is null
     * @see CandlestickPriceType
     */
    public MABuilder setCandlestickPriceType(CandlestickPriceType candlestickPriceType){
        if(candlestickPriceType == null){
            throw new NullPointerException("CandlestickPriceType must not be null");
        }
        this.candlestickPriceType = candlestickPriceType;
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
     * @throws RuntimeException if cannot find the class or if cannot create object of the concrete type
     * @see Indicator
     */
    public Indicator build(){
        InstrumentCandlesRequest request = createRequest();
        CandlesUpdater updater = new CandlesUpdater(this.ctx, request, this.candleTimeFrame);

        String className = this.maType.toString().charAt(0) + this.maType.toString().toLowerCase().substring(1) +"MA";
        Object object = null;
        try {
            Class<?> aClass = Class.forName(MA_LOCATION + className);
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(long.class, CandlestickPriceType.class, CandlesUpdater.class);
            object = declaredConstructor.newInstance(period, candlestickPriceType, updater);
            return (Indicator) object;

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create Candle Request Object
     * @return {@link InstrumentCandlesRequest} new request object for the OANDA api
     * @see InstrumentCandlesRequest
     */
    private InstrumentCandlesRequest createRequest(){

        long candlesCount = this.period * PERIOD_MULTIPLIER + 2L;

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
