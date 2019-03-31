package trader.indicator.ma;

import trader.indicator.CandlesUpdatable;
import trader.candlestick.candle.CandlePriceType;
import trader.indicator.CandlesUpdaterConnector;
import trader.exception.NoSuchConnectorException;
import trader.exception.NullArgumentException;
import trader.exception.OutOfBoundaryException;
import trader.exception.WrongIndicatorSettingsException;
import trader.indicator.Indicator;
import trader.indicator.updater.*;
import trader.indicator.ma.enums.MAType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


public final class MovingAverageBuilder {

    private static final long DEFAULT_INDICATOR_PERIOD = 20L;
    private static final long MIN_INDICATOR_PERIOD = 1L;
    private static final long MAX_INDICATOR_PERIOD = 4000L;
    private static final CandlePriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlePriceType.CLOSE;
    private static final MAType DEFAULT_MA_TYPE = MAType.SIMPLE;
    private static final String MA_LOCATION = "trader.indicator.ma.";
    private static final int SETTABLE_FIELDS_COUNT = 3;

    private long indicatorPeriod;
    private CandlePriceType candlePriceType;
    private MAType maType;
    private CandlesUpdaterConnector updaterConnector;
////////////////////////////////////////////////////////
    public MovingAverageBuilder(CandlesUpdaterConnector updaterConnector) {
        setCandlesUpdaterConnector(updaterConnector);
        setDefaults();
    }
///////////////////////////////////////////////
    public MovingAverageBuilder() {
    //    setCandlesUpdaterConnector(updaterConnector);
        setDefaults();
    }

    public MovingAverageBuilder setPeriod(long period){
        if (periodIsOutOfBoundary(period))
            throw new OutOfBoundaryException();
        indicatorPeriod = period;
        return this;
    }

    public MovingAverageBuilder setCandlePriceType(CandlePriceType candlePriceType){
        if(candlePriceType == null)
            throw new NullArgumentException();
        this.candlePriceType = candlePriceType;
        return this;
    }

    public MovingAverageBuilder setMAType(MAType maType){
        if(maType == null)
            throw new NullArgumentException();
        this.maType = maType;
        return this;
    }
///////////////////////////////////////remove///////////////////////////////////////////////
    public Indicator build(){
        return instantiatesIndicator(createCandlesUpdater());
    }

    public Indicator build(String[] settings){
        if(settings == null || settings.length != SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(Long.parseLong(settings[0]));
        setCandlePriceType(CandlePriceType.valueOf(settings[1]));
        setMAType(MAType.valueOf(settings[2]));
        return instantiatesIndicator(createCandlesUpdater());
    }
/////////////////////////////////////remove////////////////////////////////////////////
    public Indicator build(HashMap<String, String> settings){
//        if(settings == null || settings.length != SETTABLE_FIELDS_COUNT)
//            throw new WrongIndicatorSettingsException();
//        setPeriod(Long.parseLong(settings[0]));
//        setCandlePriceType(CandlePriceType.valueOf(settings[1]));
//        setMAType(MAType.valueOf(settings[2]));
        return instantiatesIndicator();
    }

    private void setCandlesUpdaterConnector(CandlesUpdaterConnector connector){
        if (connector == null)
            throw new NoSuchConnectorException();
        updaterConnector = connector;
    }

    private void setDefaults() {
        this.indicatorPeriod = DEFAULT_INDICATOR_PERIOD;
        this.candlePriceType = DEFAULT_CANDLESTICK_PRICE_TYPE;
        this.maType = DEFAULT_MA_TYPE;
    }

    private boolean periodIsOutOfBoundary(long period) {
        return period < MIN_INDICATOR_PERIOD || period > MAX_INDICATOR_PERIOD;
    }
////////////////////remove////////////////////////////////////////////////////////
    private CandlesUpdatable createCandlesUpdater() {
        return new CandlesUpdater(updaterConnector);
    }

    private Indicator instantiatesIndicator(CandlesUpdatable updater)  {
        try {
            Class<?> indicatorClass = Class.forName(MA_LOCATION + composeIndicatorClassName());
            Constructor<?> indicatorConstructor = indicatorClass.getDeclaredConstructor(long.class, CandlePriceType.class, CandlesUpdatable.class);
            return (Indicator) indicatorConstructor.newInstance(indicatorPeriod, candlePriceType, updater);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
/////////////////////////////////////remove///////////////////////////////////////////////

    private Indicator instantiatesIndicator()  {
        try {
            Class<?> indicatorClass = Class.forName(MA_LOCATION + composeIndicatorClassName());
            Constructor<?> indicatorConstructor = indicatorClass.getDeclaredConstructor(long.class, CandlePriceType.class);
            return (Indicator) indicatorConstructor.newInstance(indicatorPeriod, candlePriceType);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String composeIndicatorClassName() {
        return this.maType.toString().charAt(0) + this.maType.toString().toLowerCase().substring(1) +"MovingAverage";
    }

}
