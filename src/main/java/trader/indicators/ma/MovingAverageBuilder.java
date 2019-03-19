package trader.indicators.ma;

import trader.candle.CandlesUpdater;
import trader.connectors.ApiConnector;
import trader.exceptions.NoSuchConnectorException;
import trader.exceptions.NullArgumentException;
import trader.exceptions.OutOfBoundaryException;
import trader.indicators.Indicator;
import trader.candle.CandlestickPriceType;
import trader.indicators.ma.enums.MAType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public final class MovingAverageBuilder {

    private static final long DEFAULT_INDICATOR_PERIOD = 20L;
    private static final long MIN_INDICATOR_PERIOD = 1L;
    private static final long MAX_INDICATOR_PERIOD = 4000L;
    private static final CandlestickPriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlestickPriceType.CLOSE;
    private static final MAType DEFAULT_MA_TYPE = MAType.SIMPLE;
    private static final String MA_LOCATION = "trader.indicators.ma.";

    private long indicatorPeriod;
    private CandlestickPriceType candlestickPriceType;
    private MAType maType;
    private ApiConnector apiConnector;

    public MovingAverageBuilder(ApiConnector apiConnector) {
        setApiConnector(apiConnector);
        setDefaults();
    }

    public MovingAverageBuilder setPeriod(long period){
        if (periodIsOutOfBoundary(period))
            throw new OutOfBoundaryException();
        indicatorPeriod = period;
        return this;
    }

    public MovingAverageBuilder setCandlestickPriceType(CandlestickPriceType candlestickPriceType){
        if(candlestickPriceType == null)
            throw new NullArgumentException();
        this.candlestickPriceType = candlestickPriceType;
        return this;
    }

    public MovingAverageBuilder setMAType(MAType maType){
        if(maType == null)
            throw new NullArgumentException();
        this.maType = maType;
        return this;
    }

    public Indicator build(){
        return instantiatesIndicator(createCandlesUpdater());
    }

    private void setApiConnector(ApiConnector connector){
        if (connector == null)
            throw new NoSuchConnectorException();
        apiConnector = connector;
    }

    private void setDefaults() {
        this.indicatorPeriod = DEFAULT_INDICATOR_PERIOD;
        this.candlestickPriceType = DEFAULT_CANDLESTICK_PRICE_TYPE;
        this.maType = DEFAULT_MA_TYPE;
    }

    private boolean periodIsOutOfBoundary(long period) {
        return period < MIN_INDICATOR_PERIOD || period > MAX_INDICATOR_PERIOD;
    }

    private CandlesUpdater createCandlesUpdater() {
        return new CandlesUpdater(apiConnector);
    }

    private Indicator instantiatesIndicator(CandlesUpdater updater)  {
        try {
            Class<?> indicatorClass = Class.forName(MA_LOCATION + composeIndicatorClassName());
            Constructor<?> indicatorConstructor = indicatorClass.getDeclaredConstructor(long.class, CandlestickPriceType.class, CandlesUpdater.class);
            return (Indicator) indicatorConstructor.newInstance(indicatorPeriod, candlestickPriceType, updater);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String composeIndicatorClassName() {
        return this.maType.toString().charAt(0) + this.maType.toString().toLowerCase().substring(1) +"MovingAverage";
    }

}
