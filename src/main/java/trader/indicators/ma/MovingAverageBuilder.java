package trader.indicators.ma;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import trader.candles.CandlesUpdater;
import trader.config.Config;
import trader.indicators.Indicator;
import trader.indicators.enums.CandleGranularity;
import trader.indicators.enums.CandlestickPriceType;
import trader.indicators.ma.enums.MAType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public final class MovingAverageBuilder {

    private static final long DEFAULT_CANDLESTICK_QUANTITY = 20L;
    private static final long MIN_CANDLESTICK_QUANTITY = 1L;
    private static final long MAX_CANDLESTICK_QUANTITY = 4000L;
    private static final CandleGranularity DEFAULT_CANDLE_TIME_FRAME = CandleGranularity.H4;
    private static final CandlestickPriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlestickPriceType.CLOSE;
    private static final MAType DEFAULT_MA_TYPE = MAType.SIMPLE;
    private static final long CANDLESTICK_QUANTITY_MULTIPLIER = 4L;
    private static final String MA_LOCATION = "trader.indicators.ma.";

    private Context ctx;
    private long candlestickQuantity;
    private CandleGranularity candleTimeFrame;
    private CandlestickPriceType candlestickPriceType;
    private MAType maType;

    public MovingAverageBuilder(Context context) {
        setContext(context);
        this.candlestickQuantity = DEFAULT_CANDLESTICK_QUANTITY;
        this.candleTimeFrame = DEFAULT_CANDLE_TIME_FRAME;
        this.candlestickPriceType = DEFAULT_CANDLESTICK_PRICE_TYPE;
        this.maType = DEFAULT_MA_TYPE;
    }

    public MovingAverageBuilder setCandleTimeFrame(CandleGranularity candleTimeFrame){
        if (candleTimeFrame == null)
            throw new NullPointerException("Candle time frame is null");
        this.candleTimeFrame = candleTimeFrame;
        return this;
    }

    public MovingAverageBuilder setCandlesQuantity(long candlestickQuantity){
        if (quantityIsOutOfBoundary(candlestickQuantity)){
            throw new IllegalArgumentException (
                    String.format("Quantity is %d, must be between %d and %d.",
                            candlestickQuantity, MIN_CANDLESTICK_QUANTITY, MAX_CANDLESTICK_QUANTITY)
            );
        }
        this.candlestickQuantity = candlestickQuantity;
        return this;
    }

    public MovingAverageBuilder setCandlestickPriceType(CandlestickPriceType candlestickPriceType){
        if(candlestickPriceType == null)
            throw new NullPointerException("CandlestickPriceType must not be null");
        this.candlestickPriceType = candlestickPriceType;
        return this;
    }

    public MovingAverageBuilder setMAType(MAType maType){
        if(maType == null)
            throw new NullPointerException("Type of the moving average must not be null");
        this.maType = maType;
        return this;
    }

    public Indicator build(){
        return instantiatesIndicator(createCandlesUpdater());
    }

    private void setContext(Context context){
        if (context == null)
            throw new NullPointerException("Context is null");
        this.ctx = context;
    }

    private boolean quantityIsOutOfBoundary(long candlestickQuantity) {
        return candlestickQuantity < MIN_CANDLESTICK_QUANTITY || candlestickQuantity > MAX_CANDLESTICK_QUANTITY;
    }

    /**
     * Create Candle Request Object
     * @return {@link InstrumentCandlesRequest} new request object for the OANDA api
     * @see InstrumentCandlesRequest
     */
    private InstrumentCandlesRequest createCandlesRequest(){

        return new InstrumentCandlesRequest(Config.INSTRUMENT)
                .setCount(calculateCandlesQuantity())
                .setGranularity(this.candleTimeFrame.extractOANDAGranularity())
                .setSmooth(false);
    }

    private long calculateCandlesQuantity() {
        long offset = 2L;
        return this.candlestickQuantity * CANDLESTICK_QUANTITY_MULTIPLIER + offset;
    }

    private CandlesUpdater createCandlesUpdater() {
        return new CandlesUpdater(this.ctx, createCandlesRequest(), this.candleTimeFrame);
    }

    private Indicator instantiatesIndicator(CandlesUpdater updater) {
        try {
            Class<?> indicatorClass = Class.forName(MA_LOCATION + composeIndicatorClassName());
            Constructor<?> indicatorConstructor = indicatorClass.getDeclaredConstructor(long.class, CandlestickPriceType.class, CandlesUpdater.class);
            return (Indicator) indicatorConstructor.newInstance(candlestickQuantity, candlestickPriceType, updater);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String composeIndicatorClassName() {
        return this.maType.toString().charAt(0) + this.maType.toString().toLowerCase().substring(1) +"MovingAverage";
    }

}
