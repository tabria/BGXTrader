package trader.indicators;

import com.oanda.v20.primitives.DateTime;
import trader.core.Observer;


import java.math.BigDecimal;

public final class IndicatorObserver implements Observer {

    private final Indicator indicator;

    private IndicatorObserver(Indicator indicator){
        if(indicator == null)
            throw new IllegalArgumentException("Indicator must not be null");
        this.indicator = indicator;
    }

    public static IndicatorObserver create(Indicator indicator){
        return new IndicatorObserver(indicator);
    }

    @Override
    public void updateObserver(DateTime lastCandleTime, BigDecimal ask, BigDecimal bid) {
        if (lastCandleTime == null)
            throw new NullPointerException("DateTime is null");
        this.indicator.updateIndicator(lastCandleTime);
    }
}
