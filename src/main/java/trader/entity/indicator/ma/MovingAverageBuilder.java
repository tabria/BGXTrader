package trader.entity.indicator.ma;

import trader.entity.indicator.BaseIndicatorBuilder;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.exception.WrongIndicatorSettingsException;
import trader.entity.indicator.Indicator;
import trader.entity.indicator.ma.enums.MAType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;


public final class MovingAverageBuilder extends BaseIndicatorBuilder {

    private static final MAType SETTINGS_DEFAULT_MA_TYPE = MAType.SIMPLE;
    private static final String SETTINGS_MA_TYPE_KEY_NAME = "maType";
    private static final String MA_LOCATION = "trader.entity.indicator.ma.";
    private static final int SETTABLE_FIELDS_COUNT = 3;
    private static final String MOVING_AVERAGE = "MovingAverage";

    private MAType maType;

    public Indicator build(HashMap<String, String> settings){
        if (settings == null || settings.size() > SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(settings);
        setCandlePriceType(settings);
        setMAType(settings);
        return instantiatesIndicator();
    }

    public MovingAverageBuilder setMAType(HashMap<String, String> settings) {
        this.maType = parseMAType(settings);
        return this;
    }

    private MAType parseMAType(HashMap<String, String> settings) {
        if (isNotDefault(settings, SETTINGS_MA_TYPE_KEY_NAME)) {
            try {
                return MAType.valueOf(settings.get(SETTINGS_MA_TYPE_KEY_NAME).trim().toUpperCase());
            } catch (Exception e) {
                throw new WrongIndicatorSettingsException();
            }
        }
        return SETTINGS_DEFAULT_MA_TYPE;
    }

    private Indicator instantiatesIndicator()  {
        try {
            Class<?> indicatorClass = Class.forName(MA_LOCATION + composeIndicatorClassName());
            Constructor<?> indicatorConstructor = indicatorClass.getDeclaredConstructor(long.class, CandlePriceType.class, List.class);
            return (Indicator) indicatorConstructor.newInstance(indicatorPeriod, candlePriceType, candlestickList);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String composeIndicatorClassName() {
        return this.maType.toString().charAt(0) + this.maType.toString().toLowerCase().substring(1) + MOVING_AVERAGE;
    }

}
