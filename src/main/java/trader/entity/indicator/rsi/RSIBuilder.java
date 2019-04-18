package trader.entity.indicator.rsi;

import trader.exception.*;
import trader.entity.indicator.BaseIndicatorBuilder;
import trader.entity.indicator.Indicator;
import java.util.HashMap;
import java.util.Map;


public final class RSIBuilder extends BaseIndicatorBuilder {

    public Indicator build(Map<String, String> settings){
        if (settings == null)
            throw new WrongIndicatorSettingsException();
        setPeriod(settings);
        setCandlePriceType(settings);
        setGranularity(settings);
        return new RelativeStrengthIndex(indicatorPeriod, candlePriceType, granularity);
    }

}
