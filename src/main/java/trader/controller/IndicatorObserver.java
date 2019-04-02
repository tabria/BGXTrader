package trader.controller;

import trader.exception.NullArgumentException;
import trader.entity.indicator.Indicator;
import trader.price.Pricing;

import java.math.BigDecimal;
import java.util.List;

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
    public void updateObserver(Pricing price) {
        if (price == null)
            throw new NullArgumentException();
        this.indicator.updateIndicator();
    }
}
