package trader.entity.indicator.rsi;

import trader.exception.*;
import trader.entity.indicator.BaseIndicatorBuilder;
import trader.entity.indicator.Indicator;
import java.util.HashMap;


public final class RSIBuilder extends BaseIndicatorBuilder {

  //  private static final int SETTABLE_FIELDS_COUNT = 3;

    public Indicator build(HashMap<String, String> settings){
        if (settings == null)//|| settings.size() > SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(settings);
        setCandlePriceType(settings);
        setGranularity(settings);
        return new RelativeStrengthIndex(indicatorPeriod, candlePriceType, granularity);
    }

}
