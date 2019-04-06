package trader.controller;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.controller.enums.SettingsFieldNames;
import trader.exception.NullArgumentException;
import trader.entity.indicator.Indicator;
import trader.price.Price;

import java.util.HashMap;

public final class IndicatorObserver implements Observer {

    private static final String INSTRUMENT = SettingsFieldNames.INSTRUMENT.toString();
    private static final String QUANTITY = SettingsFieldNames.QUANTITY.toString();
    private static final String GRANULARITY = SettingsFieldNames.GRANULARITY.toString();
    private final Indicator indicator;
    private final TradingStrategyConfiguration configuration;
    private HashMap<String, String> settings;

    //to be tested
    private BrokerGateway connector;

    private IndicatorObserver(Indicator indicator, TradingStrategyConfiguration configuration){
        if(indicator == null || configuration == null)
            throw new NullArgumentException();
        this.indicator = indicator;
        this.configuration = configuration;
        this.settings = initializeSettings();
    }

    public static IndicatorObserver create(Indicator indicator, TradingStrategyConfiguration configuration){
        return new IndicatorObserver(indicator, configuration);
    }

    @Override
    public void updateObserver(Price price) {
        if (price == null)
            throw new NullArgumentException();
        setUpdateQuantityInSettings();
        //call connector to get list of candles
       // controller.execute(settings);
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
