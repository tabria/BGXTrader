package trader.entity.indicator.ma;

import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.indicator.BaseIndicatorBuilder;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.exception.WrongIndicatorSettingsException;
import trader.entity.indicator.Indicator;
import trader.entity.indicator.ma.enums.MAType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public final class MovingAverageBuilder extends BaseIndicatorBuilder {

    private static final MAType SETTINGS_DEFAULT_MA_TYPE = MAType.SIMPLE;
    private static final String SETTINGS_MA_TYPE_KEY_NAME = "type";
    private static final String MA_LOCATION = "trader.entity.indicator.ma.";
    private static final String MOVING_AVERAGE = "MovingAverage";

    private MAType maType;

    public Indicator build(Map<String, String> settings){
        if (settings == null)
            throw new WrongIndicatorSettingsException();
        setPeriod(settings);
        setCandlePriceType(settings);
        setGranularity(settings);
        setMAType(settings);
        setPosition(settings);
        return instantiatesIndicator();
    }

    public MovingAverageBuilder setMAType(Map<String, String> settings) {
        this.maType = parseMAType(settings);
        return this;
    }

    private MAType parseMAType(Map<String, String> settings) {
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
            Constructor<?> indicatorConstructor = indicatorClass.getDeclaredConstructor(long.class, CandlePriceType.class, CandleGranularity.class, String.class);
            return (Indicator) indicatorConstructor.newInstance(indicatorPeriod, candlePriceType, granularity, position);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String composeIndicatorClassName() {
        return this.maType.toString().charAt(0) + this.maType.toString().toLowerCase().substring(1) + MOVING_AVERAGE;
    }

}
