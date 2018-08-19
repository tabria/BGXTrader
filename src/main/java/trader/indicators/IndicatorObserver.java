package trader.indicators;

import com.oanda.v20.primitives.DateTime;
import trader.core.Observer;
import trader.prices.PriceObservable;


import java.math.BigDecimal;

/**
 *  This class is an Observer wrapper for Indicators
 */

public final class IndicatorObserver implements Observer {

    private final Indicator indicator;

    private IndicatorObserver(Indicator indicator){
        this.indicator = indicator;
    }

    public static IndicatorObserver create(Indicator indicator){
        return new IndicatorObserver(indicator);
    }

    /**
     * Update observers
     * @param dateTime this is the time of the last fetched candle
     * @see DateTime
     * @see PriceObservable
     */
    @Override
    public void updateObserver(DateTime dateTime, BigDecimal ask, BigDecimal bid) {
        if (dateTime == null){
            throw new NullPointerException("DateTime is null");
        }
        this.indicator.update(dateTime, ask, bid);
    }
}
