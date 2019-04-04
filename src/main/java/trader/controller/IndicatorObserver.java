package trader.controller;

import trader.exception.NullArgumentException;
import trader.entity.indicator.Indicator;
import trader.price.Pricing;

public final class IndicatorObserver implements Observer {

    private final Indicator indicator;
    private final UpdateIndicatorController controller;

    private IndicatorObserver(Indicator indicator, UpdateIndicatorController controller){
        if(indicator == null || controller == null)
            throw new NullArgumentException();
        this.indicator = indicator;
        this.controller = controller;
    }

    public static IndicatorObserver create(Indicator indicator, UpdateIndicatorController controller){
        return new IndicatorObserver(indicator, controller);
    }

    @Override
    public void updateObserver(Pricing price) {
        if (price == null)
            throw new NullArgumentException();
        this.indicator.updateIndicator();
    }
}
