package trader.controller;

import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;
import trader.entity.indicator.Indicator;
import trader.price.Price;

import java.util.HashMap;

public final class IndicatorObserver implements Observer {

    private static final String INSTRUMENT = "instrument";
    private static final String QUANTITY = "quantity";
    private static final String GRANULARITY = "granularity";
    private final Indicator indicator;
    private final UpdateIndicatorController controller;
    private TradingStrategyConfiguration configuration;
    private HashMap<String, String> settings;

    private IndicatorObserver(Indicator indicator, UpdateIndicatorController controller , TradingStrategyConfiguration configuration){
        if(indicator == null || controller == null || configuration == null)
            throw new NullArgumentException();
        this.indicator = indicator;
        this.controller = controller;
        this.configuration = configuration;
        this.settings = initializeSettings();
    }

    public static IndicatorObserver create(Indicator indicator, UpdateIndicatorController controller, TradingStrategyConfiguration configuration){
        return new IndicatorObserver(indicator, controller, configuration);
    }

    @Override
    public void updateObserver(Price price) {
        if (price == null)
            throw new NullArgumentException();
        setUpdateQuantityInSettings();
        controller.execute(settings);
    }

    private void setUpdateQuantityInSettings() {
        if(indicator.getValues().size() != 0)
            settings.put(QUANTITY, String.valueOf(configuration.getUpdateCandlesQuantity()));
    }

    private HashMap<String, String> initializeSettings(){
        HashMap<String, String> initialSettings = new HashMap<>();
        initialSettings.put(INSTRUMENT, configuration.getInstrument());
        initialSettings.put(QUANTITY, String.valueOf(configuration.getInitialCandlesQuantity()));
        initialSettings.put(GRANULARITY, indicator.getGranularity().toString());
        return initialSettings;
    }
}
