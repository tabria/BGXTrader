package trader.observer;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.controller.enums.SettingsFieldNames;
import trader.entity.candlestick.Candlestick;
import trader.exception.NullArgumentException;
import trader.entity.indicator.Indicator;
import trader.price.Price;

import java.util.HashMap;
import java.util.List;

public final class UpdateIndicatorObserver extends BaseObserver {

    private static final String INSTRUMENT = SettingsFieldNames.INSTRUMENT.toString();
    private static final String QUANTITY = SettingsFieldNames.QUANTITY.toString();
    private static final String GRANULARITY = SettingsFieldNames.GRANULARITY.toString();


    private final Indicator indicator;
    private final TradingStrategyConfiguration configuration;
    private HashMap<String, String> settings;

    public UpdateIndicatorObserver(Indicator indicator, TradingStrategyConfiguration configuration, BrokerGateway gateway){
        super(gateway);
        if(configuration == null || indicator == null)
            throw new NullArgumentException();
        this.configuration = configuration;
        this.indicator = indicator;
        this.settings = initializeSettings();
    }

    @Override
    public void updateObserver(Price price) {
        if (price == null)
            throw new NullArgumentException();
        setUpdateQuantityInSettings();
        List<Candlestick> candles = brokerGateway.getCandles(settings);
        indicator.updateIndicator(candles);
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
