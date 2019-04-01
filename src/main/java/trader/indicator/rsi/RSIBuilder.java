package trader.indicator.rsi;

import trader.exception.*;
import trader.indicator.BaseIndicatorBuilder;
import trader.indicator.Indicator;
import java.util.HashMap;


public final class RSIBuilder extends BaseIndicatorBuilder {

    private static final int SETTABLE_FIELDS_COUNT = 2;

    public Indicator build(HashMap<String, String> settings){
        if (settings == null || settings.size() > SETTABLE_FIELDS_COUNT)
            throw new WrongIndicatorSettingsException();
        setPeriod(settings);
        setCandlePriceType(settings);
        return new RelativeStrengthIndex(indicatorPeriod, candlePriceType, candlestickList);
    }

}
